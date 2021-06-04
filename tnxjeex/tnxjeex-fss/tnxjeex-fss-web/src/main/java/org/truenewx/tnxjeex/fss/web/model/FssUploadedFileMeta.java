package org.truenewx.tnxjeex.fss.web.model;

import org.truenewx.tnxjeex.fss.model.FssFileMeta;

/**
 * 已上传的文件元数据
 *
 * @author jianglei
 */
public class FssUploadedFileMeta extends FssFileMeta {

    private String id;

    public FssUploadedFileMeta(String id, String name, String storageUrl, String readUrl, String thumbnailReadUrl) {
        super(name, storageUrl, readUrl, thumbnailReadUrl);
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
