package org.truenewx.tnxjee;

import org.springframework.context.annotation.ComponentScan;

/**
 * 框架信息
 *
 * @author jianglei
 */
@ComponentScan(basePackageClasses = Framework.class)
public class Framework {

    /**
     * 框架名称
     */
    public static final String NAME = "tnxjee";

}
