package org.truenewx.tnxjeex.doc.ppt.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.PPDrawing;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;
import org.truenewx.tnxjeex.doc.core.DocOutline;
import org.truenewx.tnxjeex.doc.core.util.DocUtil;

/**
 * PPT文档
 *
 * @author jianglei
 */
public class PptDoc {

    private static final String TEXT_PROP_NAME__FONT_INDEX = "font.index";

    private SlideShow<?, ?> origin;
    private String defaultFontFamily = "SimSun"; // 默认字体：宋体，改动时需为中文字体

    public PptDoc(InputStream in, String extension) {
        extension = DocUtil.standardizeExtension(extension);
        try {
            if (FileExtensions.PPT.equals(extension)) {
                this.origin = new HSLFSlideShow(in);
            } else {
                this.origin = new XMLSlideShow(in);
            }
        } catch (IOException e) {
            throw new BusinessException(DocExceptionCodes.CAN_NOT_LOAD, FileExtensions.PPTX.toUpperCase());
        }
    }

    public SlideShow<?, ?> getOrigin() {
        return this.origin;
    }

    public void setDefaultFontFamily(String defaultFontFamily) {
        Assert.isTrue(StringUtil.isChineseFontFamily(defaultFontFamily),
                "The 'defaultFontFamily' must be a chinese font family");
        this.defaultFontFamily = defaultFontFamily;
    }

    public DocOutline getOutline() {
        DocOutline outline = new DocOutline();
        outline.setPageCount(this.origin.getSlides().size());
        return outline;
    }

    public BufferedImage renderImage(int pageIndex) {
        return renderImage(pageIndex, 1);
    }

    @SuppressWarnings("unchecked")
    public BufferedImage renderImage(int pageIndex, float scale) {
        Dimension pageSize = this.origin.getPageSize();
        List<? extends Slide<?, ?>> slides = this.origin.getSlides();
        if (0 <= pageIndex && pageIndex < slides.size()) {
            Slide<?, ?> slide = slides.get(pageIndex);

            List<org.apache.poi.sl.usermodel.Shape<?, ?>> shapes = (List<org.apache.poi.sl.usermodel.Shape<?, ?>>) slide
                    .getShapes();
            for (Shape<?, ?> shape : shapes) {
                processFont(shape);
            }

            BufferedImage image = new BufferedImage((int) (pageSize.width * scale), (int) (pageSize.height * scale),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(Drawable.BUFFERED_IMAGE, new WeakReference<>(image));
            graphics.setPaint(Color.WHITE);
            graphics.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
            graphics.scale(scale, scale);
            slide.draw(graphics);
            graphics.dispose();
            image.flush();
            return image;
        }
        return null;
    }

    private void processFont(Shape<?, ?> shape) {
        if (shape instanceof GroupShape) {
            GroupShape<?, ?> groupShape = (GroupShape<?, ?>) shape;
            for (Shape<?, ?> child : groupShape) {
                processFont(child);
            }
        } else if (shape instanceof TextShape) {
            TextShape<?, ?> textShape = (TextShape<?, ?>) shape;
            Integer fontIndex = null;
            for (TextParagraph<?, ?, ?> textParagraph : textShape) {
                for (TextRun textRun : textParagraph) {
                    String text = textRun.getRawText();
                    // 文本中包含中文但字体为非中文字体，则一律修改为默认字体，以解决中英文混杂时乱码的问题
                    if (StringUtil.containsChinese(text)) {
                        String fontFamily = textRun.getFontFamily();
                        if (!StringUtil.isChineseFontFamily(fontFamily)) {
                            textRun.setFontFamily(this.defaultFontFamily);
                            fontIndex = textRun.getFontInfo(FontGroup.LATIN).getIndex();
                        }
                    }
                }
            }
            if (fontIndex != null) {
                if (textShape instanceof HSLFTextShape) {
                    HSLFTextShape hslfTextShape = (HSLFTextShape) textShape;
                    EscherTextboxWrapper textboxWrapper = getTextboxWrapper(hslfTextShape);
                    if (textboxWrapper != null) { // 修改样式中的字体索引，该字体索引为生成图片时使用字体的标识
                        StyleTextPropAtom styleTextPropAtom = textboxWrapper.getStyleTextPropAtom();
                        List<TextPropCollection> characterStyles = styleTextPropAtom.getCharacterStyles();
                        for (TextPropCollection textPropCollection : characterStyles) {
                            boolean modified = false;
                            List<TextProp> textPropList = textPropCollection.getTextPropList();
                            for (TextProp textProp : textPropList) {
                                if (textProp.getName().endsWith(TEXT_PROP_NAME__FONT_INDEX)) {
                                    textProp.setValue(fontIndex);
                                    modified = true;
                                }
                            }
                            if (!modified) { // 如果样式中原本没有字体索引，则加入字体索引以备用
                                textPropCollection.addWithName(TEXT_PROP_NAME__FONT_INDEX).setValue(fontIndex);
                            }
                        }
                    }
                }
            }
        }
    }

    private EscherTextboxWrapper getTextboxWrapper(HSLFTextShape hslfTextShape) {
        PPDrawing drawing = hslfTextShape.getSheet().getPPDrawing();
        if (drawing != null) {
            EscherTextboxWrapper textboxWrapper = null;
            EscherTextboxWrapper[] wrappers = drawing.getTextboxWrappers();
            if (wrappers != null) {
                EscherTextboxRecord textboxRecord = hslfTextShape.getEscherChild(EscherTextboxRecord.RECORD_ID);
                for (EscherTextboxWrapper wrapper : wrappers) {
                    if (textboxRecord == wrapper.getEscherRecord()) {
                        textboxWrapper = wrapper;
                        break;
                    }
                }
            }
            // 当在drawing中找不到对应的textboxWrapper时，取出文本形状中的textboxWrapper加入drawing中，
            // 以解决在此之前修改的字体，在后续导出为图片时不生效的问题。
            // TODO 由于HSLFTextShape未提供获取textboxWrapper的支持，使用了反射机制获取，后续版本更新时需检查是否有变化
            if (textboxWrapper == null) {
                textboxWrapper = BeanUtil.getFieldValue(hslfTextShape, "_txtbox");
                drawing.addTextboxWrapper(textboxWrapper);
            }
            return textboxWrapper;
        }
        return null;
    }

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
