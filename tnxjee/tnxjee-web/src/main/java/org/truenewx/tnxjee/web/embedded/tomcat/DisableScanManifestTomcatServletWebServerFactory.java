package org.truenewx.tnxjee.web.embedded.tomcat;

import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * 禁用Manifest扫描的TomcatServletWebServerFactory，用于解决引入poi-5后内嵌tomcat初始化加载无法找到batik-xml-*.jar文件的问题
 *
 * @author jianglei
 */
@Component
public class DisableScanManifestTomcatServletWebServerFactory extends TomcatServletWebServerFactory {

    @Override
    protected void postProcessContext(org.apache.catalina.Context context) {
        JarScanner jarScanner = context.getJarScanner();
        if (jarScanner instanceof StandardJarScanner) {
            ((StandardJarScanner) jarScanner).setScanManifest(false);
        }
    }

}
