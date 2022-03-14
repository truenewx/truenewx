package org.truenewx.tnxjeex.file.pdf;

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
import org.truenewx.tnxjee.core.util.ImageUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.file.core.doc.DocCatalog;
import org.truenewx.tnxjeex.file.core.doc.DocCatalogItem;

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
            this.renderer = new PDFRenderer(this.origin);
        } catch (IOException e) {
            throw new BusinessException(PdfExceptionCodes.CAN_NOT_LOAD);
        }
    }

    public PdfDoc(InputStream in) {
        try {
            this.origin = PDDocument.load(in);
            this.renderer = new PDFRenderer(this.origin);
        } catch (IOException e) {
            throw new BusinessException(PdfExceptionCodes.CAN_NOT_LOAD);
        }
    }

    public PDDocument getOrigin() {
        return this.origin;
    }

    public DocCatalog getCatalog() {
        DocCatalog catalog = new DocCatalog();
        catalog.setPageCount(this.origin.getNumberOfPages());
        List<DocCatalogItem> items = new ArrayList<>();
        PDDocumentOutline outline = this.origin.getDocumentCatalog().getDocumentOutline();
        if (outline != null) {
            PDOutlineItem outlineItem = outline.getFirstChild();
            while (outlineItem != null) {
                items.add(toCatalogItem(outlineItem, 1));
                outlineItem = outlineItem.getNextSibling();
            }
        }
        catalog.setItems(items);
        return catalog;
    }

    private DocCatalogItem toCatalogItem(PDOutlineItem outlineItem, int level) {
        DocCatalogItem item = new DocCatalogItem();
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
            item.addSub(toCatalogItem(outlineChild, level + 1));
        }
        return item;
    }

    public BufferedImage renderImage(int pageIndex) {
        return renderImage(pageIndex, 1);
    }

    public BufferedImage renderImage(int pageIndex, float scale) {
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

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\jiang\\Desktop\\coding\\阿里巴巴Java开发手册-2020版.pdf");
        PdfDoc doc = new PdfDoc(file);
        BufferedImage image = doc.renderImage(0);
        ImageUtil.save(image, "C:\\Users\\jiang\\Desktop\\coding\\", "阿里巴巴Java开发手册-2020版-1", "jpg");
    }

}
