package org.truenewx.tnxjee.webmvc.view.menu.parser;

import org.springframework.core.io.Resource;
import org.truenewx.tnxjee.webmvc.view.menu.model.Menu;

/**
 * 菜单解析器
 *
 * @author jianglei
 */
public interface MenuParser {

    /**
     * 解析菜单
     *
     * @param resource 配置文件输入流
     * @return 菜单
     */
    Menu parse(Resource resource);

    /**
     * 获取默认配置资源
     *
     * @return 默认配置资源
     */
    Resource getDefaultLocation();

}
