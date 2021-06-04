package org.truenewx.tnxjeex.fss.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.truenewx.tnxjee.core.api.RpcApi;

/**
 * 文件存储服务的上传器
 *
 * @author jianglei
 */
@RpcApi
public interface FssUploader {

    /**
     * 上传文件，获得存储地址
     *
     * @param type  业务类型
     * @param scope 业务范围
     * @param file  上传的文件
     * @return 存储地址
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestParam("type") String type, @RequestParam("scope") String scope,
            @RequestPart("file") MultipartFile file);

}
