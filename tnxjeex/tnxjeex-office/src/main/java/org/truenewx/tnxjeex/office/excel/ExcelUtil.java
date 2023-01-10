package org.truenewx.tnxjeex.office.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.truenewx.tnxjee.core.util.StringUtil;

/**
 * Excel工具类
 */
public class ExcelUtil {

    private ExcelUtil() {
    }

    public static void replaceEnumValueToCaption(File file, int sheetIndex, int columnIndex,
            Class<? extends Enum<?>> enumClass) throws IOException {
        String extension = StringUtil.getFileExtension(file.getName(), false);
        FileInputStream in = new FileInputStream(file);
        ExcelDoc doc = new ExcelDoc(in, extension);
        in.close();
        ExcelSheet sheet = doc.getSheetAt(sheetIndex);
        sheet.replaceEnumValueToCaption(columnIndex, enumClass);
        FileOutputStream out = new FileOutputStream(file);
        doc.write(out);
        out.close();
    }

}
