package org.truenewx.tnxjee.webmvc.view.pager;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.truenewx.tnxjee.model.query.Paged;

/**
 * 分页标签
 *
 * @author jianglei
 */
public class PagerTag extends TagSupport {

    private static final long serialVersionUID = -8236304660577964952L;

    /**
     * 分页结果
     */
    private Paged value;

    /**
     * 按钮个数
     */
    private int pageNoSpan = 3;

    /**
     * 对齐方式
     */
    private String align = "";

    /**
     * 每页显示数选项集
     */
    private String pageSizeOptions = "";

    /**
     * 跳转按钮文本
     */
    private String goText = "";

    /**
     * href跳转模板
     */
    private String tempHref = "";

    /**
     * 是否显示页码输入框
     */
    private boolean pageNoInputtable = false;

    /**
     * 附加样式
     */
    private String className = "";

    /**
     * 是否显示总记录条数
     */
    private boolean showCount = true;

    /**
     * 跳转页面的方法名
     */
    private String toPage;

    /**
     * @param pageNoSpan 按钮个数
     */
    public void setPageNoSpan(int pageNoSpan) {
        this.pageNoSpan = pageNoSpan;
    }

    /**
     * @param pageNoInputtable 是否显示页码输入框
     */
    public void setPageNoInputtable(boolean pageNoInputtable) {
        this.pageNoInputtable = pageNoInputtable;
    }

    /**
     * @param goText 跳转按钮文本
     */
    public void setGoText(String goText) {
        this.goText = goText;
    }

    /**
     * @param value 分页结果
     */
    public void setValue(Paged value) {
        this.value = value;
    }

    /**
     * @param align 对齐方式
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * @param pageSizeOptions 每页显示数选项集
     */
    public void setPageSizeOptions(String pageSizeOptions) {
        this.pageSizeOptions = pageSizeOptions;
    }

    /**
     * @param tempHref 连接模板
     */
    public void setTempHref(String tempHref) {
        this.tempHref = tempHref;
    }

    /**
     * @return 附加样式
     *
     * @author jianglei
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @param className 附加样式
     *
     * @author jianglei
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return 是否显示总记录条数
     *
     * @author jianglei
     */
    public boolean isShowCount() {
        return this.showCount;
    }

    /**
     * @param showCount 是否显示总记录条数
     *
     * @author jianglei
     */
    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
    }

    /**
     *
     * @param toPage 跳转页面的方法名
     */
    public void setToPage(String toPage) {
        this.toPage = toPage;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        Map<String, Object> params = new HashMap<>();
        params.put("align", this.align);
        params.put("goText", this.goText);
        params.put("tempHref", this.tempHref);
        params.put("pageNoInputtable", this.pageNoInputtable);
        params.put("pageNoSpan", this.pageNoSpan);
        params.put("pageSizeOptions", this.pageSizeOptions);
        params.put("paging", this.value);
        params.put("className", this.className);
        params.put("showCount", this.showCount);
        params.put("toPage", this.toPage);
        PagerUtil.output((HttpServletRequest) this.pageContext.getRequest(), out, params);
        return Tag.EVAL_PAGE;
    }

}
