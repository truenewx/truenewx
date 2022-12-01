package org.truenewx.tnxjee.webmvc.view.pager;

import java.io.File;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.ExceptionUtil;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.model.query.Paged;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 分页工具类
 *
 * @author jianglei
 */
public class PagerUtil {

    private PagerUtil() {
    }

    public static int getPageSize(HttpServletRequest request, int defaultPageSize) {
        String url = WebUtil.getRelativeRequestUrl(request);
        String cookieName = url.replace('/', '_') + "_pageSize";
        String value = WebUtil.getCookieValue(request, cookieName);
        return MathUtil.parseInt(value, defaultPageSize);
    }

    public static int getPageSize(int defaultPageSize) {
        return getPageSize(SpringWebContext.getRequest(), defaultPageSize);
    }

    public static void output(HttpServletRequest request, Writer out, Map<String, Object> params) {
        Paged paged = null;
        if (params.get("paged") != null) {
            paged = (Paged) params.get("paged");
        } else if (params.get("total") != null && params.get("pageSize") != null
                && params.get("pageNo") != null) {
            paged = new Paged(MathUtil.parseInt(params.get("pageSize").toString()),
                    MathUtil.parseInt(params.get("pageNo").toString()),
                    MathUtil.parseInt(params.get("total").toString()));
        }
        if (paged != null && paged.isPageable()) {
            boolean pageNoInputtable = false;
            if (params.get("pageNoInputtable") != null) {
                pageNoInputtable = BooleanUtils
                        .toBoolean(params.get("pageNoInputtable").toString());
            }
            String goText = "";
            if (params.get("goText") != null) {
                goText = params.get("goText").toString();
            }
            String align = "";
            if (params.get("align") != null) {
                align = params.get("align").toString();
            }
            String pageSizeOptions = "";
            if (params.get("pageSizeOptions") != null) {
                pageSizeOptions = params.get("pageSizeOptions").toString();
            }
            int pageNoSpan = 3;
            if (params.get("pageNoSpan") != null) {
                pageNoSpan = Integer.parseInt(params.get("pageNoSpan").toString());
            }

            params.put("align", align);
            params.put("pageNoInputtable", pageNoInputtable);
            params.put("goText", goText);
            params.put("pageSizeOptions", pageSizeOptions.split(","));
            params.put("total", paged.getTotal());
            params.put("pageCount", paged.getPageCount());
            params.put("pageNo", paged.getPageNo());
            params.put("pageSize", paged.getPageSize());
            params.put("previousPage", paged.getPreviousPage());
            params.put("nextPage", paged.getNextPage());
            params.put("isMorePage", paged.isMorePage());
            params.put("isCountable", paged.isCountable());
            params.put("startPage", getStartPage(paged, pageNoSpan));
            params.put("endPage", getEndPage(paged, pageNoSpan));

            Configuration config = new Configuration(Configuration.VERSION_2_3_28);
            try {
                // 在pager目录中找文件
                String baseDir = request.getSession().getServletContext().getRealPath("pager");
                File templateFile = IOUtil.findI18nFileByDir(baseDir, "pager", "ftl",
                        request.getLocale());
                if (templateFile != null) {
                    config.setDirectoryForTemplateLoading(templateFile.getParentFile());
                    Template t = config.getTemplate(templateFile.getName(), Strings.ENCODING_UTF8);
                    t.process(params, out);
                }
            } catch (Exception e) {
                throw ExceptionUtil.toRuntimeException(e);
            }
        }
    }

    /**
     * 获得开始页码
     *
     * @return 开始页码
     */
    private static int getStartPage(Paged paged, int pageNoSpan) {
        Long total = paged.getTotal();
        int pageNo = paged.getPageNo();
        if (total == null) { // 没有取总数
            if (pageNo <= pageNoSpan) {
                return 1;
            }
            return pageNo - pageNoSpan;
        } else {
            int pageCount = paged.getPageCount();
            if (pageCount <= 0 || pageCount <= pageNoSpan * 2 + 1 || pageNo - pageNoSpan <= 0) {
                return 1;
            } else if (pageCount - pageNoSpan <= pageNo && pageNo - pageNoSpan - 1 <= 0) {
                return 1;
            }
            return pageNo - pageNoSpan;
        }
    }

    /**
     * 获得结束页码
     *
     * @return 结束页码
     */
    private static int getEndPage(Paged paged, int pageNoSpan) {
        Long total = paged.getTotal();
        int pageNo = paged.getPageNo();
        int pageCount = paged.getPageCount();
        int endPage = pageNo + pageNoSpan;
        if (!paged.isMorePage()) {
            return pageNo;
        } else if (total != null) {
            if (total > 0 && endPage > pageCount) {
                return pageCount;
            } else if (total == 0) {
                return 1;
            }
        }
        return endPage;
    }
}
