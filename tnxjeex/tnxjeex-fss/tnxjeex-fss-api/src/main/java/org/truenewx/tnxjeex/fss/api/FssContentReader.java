package org.truenewx.tnxjeex.fss.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件存储服务的内容读取器
 *
 * @author jianglei
 */
public interface FssContentReader {

    @GetMapping("/read/text")
    String readText(@RequestParam("storageUrl") String storageUrl,
            @RequestParam(value = "limit", required = false, defaultValue = "0") long limit);

}
