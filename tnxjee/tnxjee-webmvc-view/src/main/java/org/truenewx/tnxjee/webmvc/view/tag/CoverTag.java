package org.truenewx.tnxjee.webmvc.view.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * 掩盖标签
 *
 * @author jianglei
 */
public class CoverTag extends SimpleTagSupport {

    private String value;
    private char coverChar = '*';

    public void setValue(String value) {
        this.value = value;
    }

    public void setCoverChar(char coverChar) {
        this.coverChar = coverChar;
    }

    @Override
    public void doTag() throws JspException, IOException {
        String result = "";
        if (StringUtils.isNotBlank(this.value)) {
            int coverIndex = -1; // 掩盖的起始位置
            int coverLength = 0; // 掩盖部分的长度
            int distLength = coverLength; // 掩盖部分在掩盖后的长度
            if (StringUtil.isIpv4(this.value)) { // ipv4掩盖最后一节为一个掩盖字符
                coverIndex = this.value.lastIndexOf(Strings.DOT) + 1;
                coverLength = this.value.length() - coverIndex;
                distLength = 1;
            } else if (StringUtil.isIpv6(this.value)) { // ipv6掩盖最后一节为一个掩盖字符
                coverIndex = this.value.lastIndexOf(Strings.DOT); // ipv6兼容ipv4时，最后为ipv4结尾
                if (coverIndex < 0) {
                    coverIndex = this.value.lastIndexOf(Strings.COLON);
                }
                coverLength = this.value.length() - (++coverIndex);
                distLength = 1;
            } else if (StringUtil.isCellphone(this.value)) { // 手机号码掩盖第四至八位
                coverIndex = 3;
                coverLength = 4;
                distLength = coverLength;
            } else if (StringUtil.isIdCardNo(this.value)) { // 身份证号码掩盖第十一至十七位
                coverIndex = 10;
                coverLength = 6;
                distLength = coverLength;
            } else if (StringUtil.isEmail(this.value)) { // Email掩盖@之前部分的中间一半字符
                String name = this.value.substring(0, this.value.indexOf(Strings.AT));
                int length = name.length();
                coverIndex = length / 4;
                coverLength = length / 2;
                distLength = coverLength;
            } else { // 一般字符掩盖中间一半字符
                int length = this.value.length();
                coverIndex = length / 4;
                coverLength = length / 2;
                distLength = coverLength;
            }
            if (coverIndex >= 0) {
                result += this.value.substring(0, coverIndex); // 先取掩盖位置之前的字符串
                for (int i = 0; i < distLength; i++) { // 拼接目标长度的掩盖字符
                    result += this.coverChar;
                }
                result += this.value.substring(coverIndex + coverLength); // 最后取掩盖位置+掩盖长度之后的字符串
            }
        }
        JspWriter out = getJspContext().getOut();
        out.print(result);
    }
}
