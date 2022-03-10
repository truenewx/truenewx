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
    private int destIndex = -1;
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

    public int getDestIndex() {
        return this.destIndex;
    }

    public void setDestIndex(int destIndex) {
        this.destIndex = destIndex;
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
