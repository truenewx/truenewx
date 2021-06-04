package org.truenewx.tnxjeex.fss.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxjeex.fss.api.model.FssTransferCommand;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;

/**
 * 文件存储服务的元数据解决器
 */
@RpcApi
public interface FssMetaResolver {

    /**
     * 当前用户获取指定存储URL对应的文件元数据
     *
     * @param storageUrl 存储URL
     * @return 文件元数据
     */
    @GetMapping("/meta")
    FssFileMeta resolveMeta(@RequestParam("storageUrl") String storageUrl);

    /**
     * 当前用户获取指定存储URL集对应的文件元数据集
     *
     * @param storageUrls 存储URL集
     * @return 文件元数据集
     */
    @GetMapping("/metas")
    FssFileMeta[] resolveMetas(@RequestParam("storageUrls") String[] storageUrls);

    /**
     * 根据内部存储地址获取外部读取地址<br/>
     * 可能有三种格式的结果：<br/>
     * 1./${contextPath}开头，相对当前主机地址的相对路径，调用者如想获得绝对地址需加上当前主机地址；<br/>
     * 2.http://或https://开头，包含主机地址且指定了访问协议的绝对地址；<br/>
     * 3.//开头，包含主机地址但不指定访问协议，允许使用http和https中的任意一种协议访问
     *
     * @param storageUrl 内部存储地址
     * @param thumbnail  是否缩略图
     * @return 外部读取地址
     */
    @GetMapping("/read-url")
    String resolveReadUrl(@RequestParam(value = "storageUrl", required = false) String storageUrl,
            @RequestParam("thumbnail") boolean thumbnail);

    /**
     * 转储外部资源为内部存储资源
     *
     * @param command 提交参数体
     * @return 内部存储地址
     */
    @PostMapping("/transfer")
    String transfer(@RequestBody FssTransferCommand command);

}
