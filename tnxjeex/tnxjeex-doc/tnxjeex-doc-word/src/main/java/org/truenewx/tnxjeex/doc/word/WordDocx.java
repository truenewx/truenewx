package org.truenewx.tnxjeex.doc.word;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;

import fr.opensagres.poi.xwpf.converter.xhtml.Base64EmbedImgManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

/**
 * Word 2007及以上文档，扩展名为docx
 *
 * @author jianglei
 */
public class WordDocx {

    private XWPFDocument origin;

    public WordDocx(InputStream in) {
        try {
            this.origin = new XWPFDocument(in);
        } catch (IOException e) {
            throw new BusinessException(DocExceptionCodes.CAN_NOT_LOAD, FileExtensions.DOCX);
        }
    }

    public XWPFDocument getOrigin() {
        return this.origin;
    }

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

    public String convertToHtml(String encoding) throws IOException {
        XHTMLOptions options = XHTMLOptions.create().indent(4).setImageManager(new Base64EmbedImgManager());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XHTMLConverter.getInstance().convert(this.origin, out, options);
        String html = out.toString(encoding);
        html = html.replaceAll("white-space:pre-wrap;", Strings.EMPTY);
        html = html.replaceAll("\\n\\s*</span>", "</span>");
        html = html.replaceAll("</span>\\n\\s*<span ", "</span><span ");
        html = html.replaceAll("</span>\\n\\s*<span>", "</span><span>");
        return html;
    }

    public void convertToHtml(OutputStream out) {
        try {
            String html = convertToHtml(Strings.ENCODING_UTF8);
            IOUtils.write(html, out, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
