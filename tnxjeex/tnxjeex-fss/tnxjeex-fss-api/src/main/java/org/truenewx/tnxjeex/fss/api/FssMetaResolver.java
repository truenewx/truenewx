package org.truenewx.tnxjeex.fss.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.truenewx.tnxjee.core.api.RpcApi;
import org.truenewx.tnxjeex.fss.model.FssFileMeta;

/**
 * 文件存储服务的元数据解决器
 */
@RpcApi
public interface FssMetaResolver {

    /**
     * 当前用户获取指定定位地址对应的文件元数据
     *
     * @param locationUrl 定位地址
     * @return 文件元数据
     */
    @GetMapping("/meta")
    FssFileMeta resolveMeta(@RequestParam("locationUrl") String locationUrl);

    /**
     * 当前用户获取指定定位地址集对应的文件元数据集
     *
     * @param locationUrls 定位地址集
     * @return 文件元数据集
     */
    @GetMapping("/metas")
    FssFileMeta[] resolveMetas(@RequestParam("locationUrls") String[] locationUrls);

    /**
     * 根据定位地址获取外部读取地址<br>
     * 可能有三种格式的结果：<br>
     * 1./${contextPath}开头，相对当前主机地址的相对路径，调用者如想获得绝对地址需加上当前主机地址；<br>
     * 2.http://或https://开头，包含主机地址且指定了访问协议的绝对地址；<br>
     * 3.//开头，包含主机地址但不指定访问协议，允许使用http和https中的任意一种协议访问
     *
     * @param locationUrl 定位地址
     * @param thumbnail   是否缩略图
     * @return 外部读取地址
     */
    @GetMapping("/read/url")
    String resolveReadUrl(@RequestParam(value = "locationUrl", required = false) String locationUrl,
            @RequestParam("thumbnail") boolean thumbnail);

    /**
     * 根据定位地址获取下载地址，下载地址为相对于当前应用的相对读取地址，通过当前应用进行鉴权
     *
     * @param locationUrl 定位地址
     * @param absolute    返回的下载地址是否绝对地址，false-相对当前站点的地址
     * @return 下载地址
     */
    @GetMapping("/download/url")
    String resolveDownloadUrl(@RequestParam("locationUrl") String locationUrl,
            @RequestParam(value = "absolute", required = false, defaultValue = "true") boolean absolute);

}
