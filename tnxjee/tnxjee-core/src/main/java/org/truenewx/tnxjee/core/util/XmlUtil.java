package org.truenewx.tnxjee.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * XML工具类
 *
 * @author jianglei
 */
public class XmlUtil {

    private XmlUtil() {
    }

    public static void write(Document doc, File file) throws IOException {
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        format.setNewLineAfterDeclaration(false);
        XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
        writer.write(doc);
        writer.close();
    }

}
