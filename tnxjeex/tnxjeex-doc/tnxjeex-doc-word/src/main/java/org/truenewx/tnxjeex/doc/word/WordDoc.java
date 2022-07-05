package org.truenewx.tnxjeex.doc.word;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjeex.doc.core.DocExceptionCodes;
import org.w3c.dom.Document;

/**
 * Word 2003及以下文档，扩展名为doc
 *
 * @author jianglei
 */
public class WordDoc {

    private HWPFDocument origin;

    public WordDoc(InputStream in) {
        try {
            this.origin = new HWPFDocument(in);
        } catch (IOException e) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
            throw new BusinessException(DocExceptionCodes.CAN_NOT_LOAD, FileExtensions.DOC);
        }
    }

    public HWPFDocument getOrigin() {
        return this.origin;
    }

    public String convertToHtml(String encoding) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        convertToHtml(out);
        return out.toString(encoding);
    }

    public void convertToHtml(OutputStream out) {
        try {
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

            wordToHtmlConverter.setPicturesManager((content, pictureType, suggestedName, widthInches, heightInches) -> {
                return IOUtil.toBase64Data(content, pictureType.getExtension());
            });

            wordToHtmlConverter.processDocument(this.origin);
            Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer serializer = factory.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, Strings.ENCODING_UTF8);
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, FileExtensions.HTML);
            serializer.transform(domSource, streamResult);
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
    }

    public String getText() {
        WordExtractor extractor = new WordExtractor(this.origin);
        return extractor.getText();
    }

    public void close() {
        try {
            this.origin.close();
        } catch (IOException e) {
            LogUtil.error(getClass(), e);
        }
    }

}
