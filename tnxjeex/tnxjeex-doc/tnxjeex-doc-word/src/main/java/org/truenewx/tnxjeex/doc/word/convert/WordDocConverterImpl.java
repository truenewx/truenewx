package org.truenewx.tnxjeex.doc.word.convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;

/**
 * Word文档转换器实现
 *
 * @author jianglei
 */
@Component
public class WordDocConverterImpl implements WordDocConverter {

    private DocumentConverter converter;

    private DocumentConverter getConverter() {
        if (this.converter == null) {
            Map<String, Object> properties = new HashMap<>();
            this.converter = LocalConverter.builder().filterChain((context, document, chain) -> {
                chain.doFilter(context, document);
            }).storeProperties(properties).build();
        }
        return this.converter;
    }

    @Override
    public void convertToPdf(InputStream in, String extension, OutputStream out) {
        try {
            if (FileExtensions.DOCX.equals(extension) || FileExtensions.DOC.equals(extension)) {
                DocumentFormat sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(extension);
                if (sourceFormat != null) {
                    getConverter()
                            .convert(in).as(sourceFormat)
                            .to(out).as(DefaultDocumentFormatRegistry.PDF)
                            .execute();
                }
            }
        } catch (OfficeException e) {
            LogUtil.error(getClass(), e);
            if (FileExtensions.DOC.equals(extension)) {
                throw new BusinessException(DocExceptionCodes.RECOMMEND_CONVERT_FORMAT, extension, FileExtensions.DOCX);
            }
        }
    }

}
