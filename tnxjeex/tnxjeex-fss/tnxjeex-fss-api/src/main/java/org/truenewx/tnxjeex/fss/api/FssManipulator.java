package org.truenewx.tnxjeex.fss.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxjeex.fss.api.model.FssTransferCommand;

/**
 * 文件存储服务操纵器
 *
 * @author jianglei
 */
@RpcApi
public interface FssManipulator {

    /**
     * 转储外部资源为内部存储资源
     *
     * @param command 提交参数体
     * @return 定位地址
     */
    @PostMapping("/transfer")
    String transfer(@RequestBody FssTransferCommand command);

    /**
     * 上传文件，获得定位地址
     *
     * @param type  业务类型
     * @param scope 业务范围
     * @param file  上传的文件
     * @return 定位地址
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestParam("type") String type, @RequestParam("scope") String scope,
            @RequestPart("file") MultipartFile file);

    @PostMapping("/delete")
    void delete(@RequestParam("locationUrl") String locationUrl);

    @PostMapping("/delete-multi")
    void delete(@RequestParam("locationUrls") String[] locationUrls);

    @PostMapping("/copy")
    String copy(String sourceLocationUrl, String targetType, String targetScope);

    @PostMapping("/move")
    String move(String sourceLocationUrl, String targetType, String targetScope);

}
