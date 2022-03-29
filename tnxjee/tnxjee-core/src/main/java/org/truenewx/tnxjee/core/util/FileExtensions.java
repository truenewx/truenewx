package org.truenewx.tnxjee.core.util;

/**
 * 文件扩展名常量集类
 */
public class FileExtensions {

    private FileExtensions() {
    }

    // 图片
    public static final String BMP = "bmp";
    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String PNG = "png";
    public static final String SVG = "svg";
    public static final String GIF = "gif";
    public static final String[] IMAGES_NORMAL = { BMP, JPG, JPEG, PNG, SVG };
    public static final String[] IMAGES_ALL = { BMP, JPG, JPEG, PNG, SVG, GIF };

    // PDF文档
    public static final String PDF = "pdf";

    // Office文档
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String DOC = "doc";
    public static final String DOCX = "docx";
    public static final String PPT = "ppt";
    public static final String PPTX = "pptx";

    // 配置文件
    public static final String PROPERTIES = "properties";
    public static final String YAML = "yaml";
    public static final String YML = "yml";

}
