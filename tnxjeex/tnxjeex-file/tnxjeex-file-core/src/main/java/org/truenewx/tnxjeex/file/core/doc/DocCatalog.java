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
    /**
     * 从一级目录项开始直到最终选中目录项的索引清单
     */
    private List<Integer> selectedItemIndexes;

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

    public List<Integer> getSelectedItemIndexes() {
        return this.selectedItemIndexes;
    }

    public void setSelectedItemIndexes(List<Integer> selectedItemIndexes) {
        this.selectedItemIndexes = selectedItemIndexes;
    }

}
