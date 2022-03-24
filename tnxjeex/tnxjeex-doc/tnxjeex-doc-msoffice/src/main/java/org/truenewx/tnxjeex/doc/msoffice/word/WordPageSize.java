package org.truenewx.tnxjeex.doc.msoffice.word;

/**
 * Word文档页面尺寸
 *
 * @author jianglei
 */
public class WordPageSize {

    private double width;
    private double height;

    public WordPageSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    //////

    /**
     * 将指定长宽值转换为厘米单位值
     *
     * @param value 长宽值
     * @return 厘米单位值
     */
    public static double toCm(double value) {
        return Math.round(value / 20d / 72d * 2.54d * 100d) / 100d;
    }

}
