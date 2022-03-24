package org.truenewx.tnxjeex.doc.msoffice.word;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.MathUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;

import com.lowagie.text.pdf.PdfDocument;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

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

    public WordPageSize getPageSize() {
        CTPageSz ctPageSz = this.origin.getDocument().getBody().getSectPr().getPgSz();
        double width = MathUtil.parseDecimal(ctPageSz.getW().toString()).doubleValue();
        double height = MathUtil.parseDecimal(ctPageSz.getH().toString()).doubleValue();
        return new WordPageSize(width, height);
    }

    /**
     * 移除水印图片
     */
    public void removeWatermarkPicture() {
        XWPFHeaderFooterPolicy policy = this.origin.getHeaderFooterPolicy();
        XWPFHeader header = policy.getHeader(XWPFHeaderFooterPolicy.DEFAULT);
        if (header != null) {
            List<XWPFParagraph> paragraphs = header.getListParagraph();
            for (XWPFParagraph paragraph : paragraphs) {
                CTP ctp = paragraph.getCTP();
                CTR[] rArray = ctp.getRArray();
                for (int i = rArray.length - 1; i >= 0; i--) {
                    CTR ctr = rArray[i];
                    if (ctr.isSetRPr() && ctr.getPictArray().length > 0) {
                        ctp.removeR(i);
                    }
                }
            }
        }
    }

    public void transformToPdf(OutputStream out) {
        try {
            removeWatermarkPicture();
            PdfOptions options = PdfOptions.create();
            options.setConfiguration(pdfWriter -> {
                PdfDocument pdfDocument = BeanUtil.getFieldValue(pdfWriter, "pdf");
                pdfDocument.setMargins(0, 0, 0, 0);
            });
            PdfConverter.getInstance().convert(this.origin, out, options);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
    }

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
