package org.truenewx.tnxjeex.doc.word.convert;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Word文档转换器
 */
public interface WordDocConverter {

    void convertToPdf(InputStream in, String extension, OutputStream out);

}
