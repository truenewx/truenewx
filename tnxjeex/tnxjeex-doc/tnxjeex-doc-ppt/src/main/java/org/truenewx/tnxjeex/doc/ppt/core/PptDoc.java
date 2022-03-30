package org.truenewx.tnxjeex.doc.ppt.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
import org.apache.poi.xslf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;
import org.truenewx.tnxjeex.doc.core.DocOutline;
import org.truenewx.tnxjeex.doc.core.util.DocUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        List<? extends Slide<?, ?>> slides = this.origin.getSlides();
        if (0 <= pageIndex && pageIndex < slides.size()) {
            Slide<?, ?> slide = slides.get(pageIndex);

            List<org.apache.poi.sl.usermodel.Shape<?, ?>> shapes = (List<org.apache.poi.sl.usermodel.Shape<?, ?>>) slide
                    .getShapes();
            List<Runnable> preparedTasks = new ArrayList<>();
            for (Shape<?, ?> shape : shapes) {
                preparedTasks.addAll(prepare(shape));
            }
            preparedTasks.forEach(Runnable::run);

            Dimension pageSize = this.origin.getPageSize();
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

    @SuppressWarnings("unchecked")
    private List<Runnable> prepare(Shape<?, ?> shape) {
        List<Runnable> preparedTasks = new ArrayList<>();
        if (shape instanceof GroupShape) {
            GroupShape<?, ?> groupShape = (GroupShape<?, ?>) shape;
            for (Shape<?, ?> child : groupShape) {
                preparedTasks.addAll(prepare(child));
            }
        } else if (shape instanceof TextShape) {
            TextShape<?, ?> textShape = (TextShape<?, ?>) shape;
            Integer fontIndex = null;
            for (TextParagraph<?, ?, ?> textParagraph : textShape) {
                List<TextRun> textRuns = (List<TextRun>) textParagraph.getTextRuns();
                if (textRuns.isEmpty()) {
                    if (textParagraph instanceof XSLFTextParagraph) {
                        XSLFTextParagraph xslfTextParagraph = (XSLFTextParagraph) textParagraph;
                        if (isMathParagraph(xslfTextParagraph)) {
                            preparedTasks.add(() -> {
                                createTextBox((XSLFShape) shape, "tnxjeex.doc.ppt.unable_to_display_formula");
                            });
                        }
                    }
                } else {
                    for (TextRun textRun : textRuns) {
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
            }
            // 使ppt格式文件的字体更改在生成图片时生效
            if (fontIndex != null && textShape instanceof HSLFTextShape) {
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
        } else if (shape.getClass() == XSLFGraphicFrame.class) {
            // 遍历完所有形状之后再执行添加
            preparedTasks.add(() -> {
                createTextBox((XSLFGraphicFrame) shape, "tnxjeex.doc.ppt.unable_to_display_smart_art");
            });
        }
        return preparedTasks;
    }

    private boolean isMathParagraph(XSLFTextParagraph xslfTextParagraph) {
        CTTextParagraph ctTextParagraph = xslfTextParagraph.getXmlObject();
        Node node = ctTextParagraph.getDomNode();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if ("m".equals(item.getLocalName())) {
                nodeList = item.getChildNodes();
                for (int j = 0; j < nodeList.getLength(); j++) {
                    item = nodeList.item(j);
                    if ("oMathPara".equals(item.getLocalName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void createTextBox(XSLFShape templateShape, String messageKey) {
        XSLFSheet sheet = templateShape.getSheet();
        XSLFTextBox textBox = sheet.createTextBox();
        textBox.setAnchor(templateShape.getAnchor());
        textBox.setText(PptUtil.getMessage(messageKey));
        textBox.setVerticalAlignment(VerticalAlignment.MIDDLE);
        textBox.setLineColor(Color.GRAY);
        XSLFTextParagraph textParagraph = textBox.getTextParagraphs().get(0);
        textParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
        XSLFTextRun textRun = textParagraph.getTextRuns().get(0);
        textRun.setFontColor(Color.GRAY);
        textRun.setFontFamily(this.defaultFontFamily);
        if (templateShape instanceof XSLFTextShape) {
            List<XSLFTextParagraph> textParagraphs = ((XSLFTextShape) templateShape).getTextParagraphs();
            if (textParagraphs.size() > 0) {
                textParagraph = textParagraphs.get(0);
                textRun.setFontSize(textParagraph.getDefaultFontSize());
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
