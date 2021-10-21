package org.truenewx.tnxjeex.fss.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxjeex.fss.api.model.FssTransferCommand;

/**
 * 文件存储服务的控制API
 *
 * @author jianglei
 */
@RpcApi
public interface FssControlApi {

    /**
     * 转储外部资源为内部存储资源
     *
     * @param command 提交参数体
     * @return 内部存储地址
     */
    @PostMapping("/transfer")
    String transfer(@RequestBody FssTransferCommand command);

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

    @GetMapping("/read")
    String read(@RequestParam("path") String path);

    @PostMapping("/delete")
    void delete(@RequestParam("storageUrl") String storageUrl);

    @PostMapping("/delete-multi")
    void delete(@RequestParam("storageUrls") String[] storageUrls);

}
