package org.truenewx.tnxjeex.fss.web.model;

import org.truenewx.tnxjeex.fss.model.FssFileMeta;

/**
 * 已上传的文件元数据
 *
 * @author jianglei
 */
public class FssUploadedFileMeta extends FssFileMeta {

    private String id;

    public FssUploadedFileMeta(String id, String storageUrl) {
        super(storageUrl);
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
