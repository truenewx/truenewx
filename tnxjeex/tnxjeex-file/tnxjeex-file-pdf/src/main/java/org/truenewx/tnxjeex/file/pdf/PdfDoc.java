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
import org.truenewx.tnxjeex.file.core.FileCatalogItem;

/**
 * PDF文档
 *
 * @author jianglei
 */
public class PdfDoc {

    private PDDocument origin;
    private PDFRenderer renderer;

    public PdfDoc(File file) throws IOException {
        this.origin = PDDocument.load(file);
        this.renderer = new PDFRenderer(this.origin);
    }

    public PdfDoc(InputStream in) throws IOException {
        this.origin = PDDocument.load(in);
        this.renderer = new PDFRenderer(this.origin);
    }

    public PDDocument getOrigin() {
        return this.origin;
    }

    /**
     * @return 目录集
     */
    public List<FileCatalogItem> getCatalogItems() {
        List<FileCatalogItem> items = new ArrayList<>();
        PDDocumentOutline outline = this.origin.getDocumentCatalog().getDocumentOutline();
        if (outline != null) {
            PDOutlineItem outlineItem = outline.getFirstChild();
            while (outlineItem != null) {
                items.add(toCatalogItem(outlineItem));
                outlineItem = outlineItem.getNextSibling();
            }
        }
        return items;
    }

    private FileCatalogItem toCatalogItem(PDOutlineItem outlineItem) {
        FileCatalogItem item = new FileCatalogItem();
        item.setCaption(outlineItem.getTitle());
        try {
            PDDestination dest = outlineItem.getDestination();
            if (dest instanceof PDPageDestination) {
                PDPageDestination pageDest = (PDPageDestination) dest;
                item.setDestIndex(pageDest.retrievePageNumber());
            }
        } catch (Exception ignored) {
        }
        // 添加子节点
        Iterable<PDOutlineItem> outlineChildren = outlineItem.children();
        for (PDOutlineItem outlineChild : outlineChildren) {
            item.addSub(toCatalogItem(outlineChild));
        }
        return item;
    }

    public int getPageNum() {
        return this.origin.getNumberOfPages();
    }

    public BufferedImage renderImage(int pageIndex) throws IOException {
        return renderImage(pageIndex, 1);
    }

    public BufferedImage renderImage(int pageIndex, float scale) throws IOException {
        return this.renderer.renderImage(pageIndex, scale);
    }

    public void close() throws IOException {
        this.origin.close();
    }

}
