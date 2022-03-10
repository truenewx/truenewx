package org.truenewx.tnxjeex.file.core.doc;

import java.util.List;

/**
 * 文档目录
 *
 * @author jianglei
 */
public class DocCatalog {

    /**
     * 总页数
     */
    private int pageCount;
    private List<DocCatalogItem> items;

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<DocCatalogItem> getItems() {
        return this.items;
    }

    public void setItems(List<DocCatalogItem> items) {
        this.items = items;
    }

}
