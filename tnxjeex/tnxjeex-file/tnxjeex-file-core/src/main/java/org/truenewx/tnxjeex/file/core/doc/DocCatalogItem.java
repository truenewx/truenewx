package org.truenewx.tnxjeex.file.core.doc;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档目录项
 *
 * @author jianglei
 */
public class DocCatalogItem {

    private int level;
    private String caption;
    private int pageIndex = -1;
    private List<DocCatalogItem> subs;

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<DocCatalogItem> getSubs() {
        return this.subs;
    }

    public void setSubs(List<DocCatalogItem> subs) {
        this.subs = subs;
    }

    //////

    public void addSub(DocCatalogItem sub) {
        if (this.subs == null) {
            this.subs = new ArrayList<>();
        }
        this.subs.add(sub);
    }

}
