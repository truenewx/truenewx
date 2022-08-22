package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.webmvc.qrcode.QrCodeGenerator;
import org.truenewx.tnxjee.webmvc.view.tagext.UiTagSupport;

import com.google.zxing.WriterException;

/**
 * 二维码显示标签
 *
 * @author liuzhiyi
 */
public class QrCodeTag extends UiTagSupport {

    /**
     * 二维码值
     */
    private String value;

    /**
     * 二维码边长像素值
     */
    private int size;

    /**
     * LOGO URL
     */
    private String logo;

    public void setValue(String value) {
        this.value = value;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public void doTag() throws JspException, IOException {
        try {
            QrCodeGenerator generator = getBeanFromApplicationContext(QrCodeGenerator.class);
            String name = generator.save(this.value, this.size, this.logo, 0);
            // 输出标签
            print("<img src=\"", getRequest().getContextPath() + "/qrcode/" + name, "\"");
            print(joinAttributes());
            print("/>", Strings.ENTER);
        } catch (WriterException e) {
            throw new JspException(e);
        }
    }

}
