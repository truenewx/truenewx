package org.truenewx.tnxjeex.doc.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocOutline;
import org.truenewx.tnxjeex.doc.core.DocOutlineItem;

/**
 * PDF文档
 *
 * @author jianglei
 */
public class PdfDoc {

    private PDDocument origin;
    private PDFRenderer renderer;

    public PdfDoc(File file) {
        try {
            this.origin = PDDocument.load(file);
        } catch (IOException e) {
            throw new BusinessException(PdfExceptionCodes.CAN_NOT_LOAD);
        }
    }

    public PdfDoc(InputStream in) {
        try {
            this.origin = PDDocument.load(in);
        } catch (IOException e) {
            throw new BusinessException(PdfExceptionCodes.CAN_NOT_LOAD);
        }
    }

    public PDDocument getOrigin() {
        return this.origin;
    }

    public DocOutline getOutline() {
        DocOutline outline = new DocOutline();
        outline.setPageCount(this.origin.getNumberOfPages());
        List<DocOutlineItem> items = new ArrayList<>();
        PDDocumentOutline pdOutline = this.origin.getDocumentCatalog().getDocumentOutline();
        if (pdOutline != null) {
            PDOutlineItem outlineItem = pdOutline.getFirstChild();
            while (outlineItem != null) {
                items.add(toOutlineItem(outlineItem, 1));
                outlineItem = outlineItem.getNextSibling();
            }
        }
        outline.setItems(items);
        return outline;
    }

    private DocOutlineItem toOutlineItem(PDOutlineItem outlineItem, int level) {
        DocOutlineItem item = new DocOutlineItem();
        item.setLevel(level);
        item.setCaption(outlineItem.getTitle());
        try {
            PDDestination dest = outlineItem.getDestination();
            if (dest instanceof PDPageDestination) {
                PDPageDestination pageDest = (PDPageDestination) dest;
                item.setPageIndex(pageDest.retrievePageNumber());
            }
        } catch (Exception ignored) {
        }
        // 添加子节点
        Iterable<PDOutlineItem> outlineChildren = outlineItem.children();
        for (PDOutlineItem outlineChild : outlineChildren) {
            item.addSub(toOutlineItem(outlineChild, level + 1));
        }
        return item;
    }

    public BufferedImage renderImage(int pageIndex) {
        return renderImage(pageIndex, 1);
    }

    public BufferedImage renderImage(int pageIndex, float scale) {
        if (this.renderer == null) {
            this.renderer = new PDFRenderer(this.origin);
        }
        try {
            return this.renderer.renderImage(pageIndex, scale);
        } catch (IOException e) {
            throw new BusinessException(PdfExceptionCodes.CAN_NOT_LOAD);
        }
    }

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
