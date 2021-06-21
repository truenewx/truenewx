package org.truenewx.tnxjee.core.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;
import org.truenewx.tnxjee.core.Strings;

/**
 * 图片工具类
 *
 * @author jianglei
 */
public class ImageUtil {

    /**
     * 图片装载器
     */
    private static final MediaTracker TRACKER = new MediaTracker(new Component() {
        private static final long serialVersionUID = 1234162663955668507L;
    });

    private ImageUtil() {
    }

    /**
     * 截取指定输入流中图片的指定矩形区域范围内的内容
     *
     * @param in         图片输入流，如果该输入流中的数据不是图片，将抛出IOException
     * @param formatName 图片格式，如："png"
     * @param x          截取矩形区域相对于图片的x轴坐标
     * @param y          截取矩形区域相对于图片的y轴坐标
     * @param width      截取矩形区域的宽度
     * @param height     截取矩形区域的高度
     * @return 截取得到的图片数据
     * @throws IOException 如果截取过程中出现IO错误
     */
    public static byte[] clip(InputStream in, String formatName, int x, int y, int width,
            int height) throws IOException {
        BufferedImage image = ImageIO.read(in);
        image = image.getSubimage(x, y, width, height);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, out);
        out.flush();
        byte[] b = out.toByteArray();
        out.close();
        return b;
    }

    public static BufferedImage clip(BufferedImage image, String formatName, int x, int y,
            int width, int height) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        if (!readers.hasNext()) {
            return null;
        }
        ImageReader reader = readers.next();
        ByteArrayInputStream in = new ByteArrayInputStream(toBytes(image, formatName));
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(x, y, width, height);
        param.setSourceRegion(rect);
        return reader.read(0, param);
    }

    /**
     * 以圆切形式裁剪指定图片<br/>
     * 注意：由于圆心坐标会在显示屏中占据一个像素位置，加上半径后，实际的圆切直径会比预期的大一个像素，不符合预期，故无法使用圆心定位方式
     *
     * @param source   被裁剪的源图片
     * @param cornerX  裁剪圆所在正方形左上角坐标x轴
     * @param cornerY  裁剪圆所在正方形左上角坐标y轴
     * @param diameter 裁剪圆直径
     * @return 裁剪出的图片
     */
    public static BufferedImage clipCircle(BufferedImage source, int cornerX, int cornerY, int diameter) {
        diameter = Math.max(diameter, 2); // 直径至少要为2，否则无法形成一个最基本的圆
        // 源图片大小
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        // 实际裁剪所在矩形的左上角起点位置，不能超出源图片范围，最小为0
        int beginX = Math.max(cornerX, 0);
        int beginY = Math.max(cornerY, 0);
        // 实际裁剪所在矩形的右下角终点位置，不能超出源图片范围，最大为源图片最大坐标位置
        int endX = Math.min(cornerX + diameter, sourceWidth);
        int endY = Math.min(cornerY + diameter, sourceHeight);

        // 创建一个合适大小的基图
        BufferedImage target = new BufferedImage(endX - beginX, endY - beginY, BufferedImage.TYPE_INT_ARGB);
        // 计算半径，需偏移0.5，基于无大小的圆心计算半径
        double radius = (diameter / 2d) - 0.5d;
        // 无大小的圆心计算坐标
        double centerX = cornerX + radius;
        double centerY = cornerY + radius;
        int nonTransparentAlpha = toAlpha(1); // 完全不透明的alpha值
        boolean sourceAlpha = source.getColorModel().hasAlpha();
        int transparentArgb = rgbToArgb(0xffffff, toAlpha(0));
        // 源图片中实际裁剪所在矩形范围内的点才需要读取
        for (int x = beginX; x < endX; x++) {
            for (int y = beginY; y < endY; y++) {
                int argb;

                double distance = distance(x, y, centerX, centerY); // 点到圆心的距离
                double ddr = distance - radius; // 距离与半径的差，用于权衡透明度
                float opacityRatio;
                if (ddr <= 0) { // 距离≤半径，则完全从源图片取色值
                    opacityRatio = 1;
                } else if (ddr >= 1) { // 距离半径差≥1，则完全透明
                    opacityRatio = 0;
                } else { // 距离超过半径但超过的不足1，则为过渡点位
                    opacityRatio = (float) (1 - ddr);
                }

                if (opacityRatio == 0) {
                    argb = transparentArgb;
                } else {
                    argb = source.getRGB(x, y);
                    if (!sourceAlpha) {  // 如果源图片不包含alpha值，则需附加alpha值
                        int alpha = nonTransparentAlpha;
                        if (opacityRatio < 1) { // 过渡点位
                            alpha = toAlpha(opacityRatio);
                        }
                        argb = rgbToArgb(argb, alpha);
                    } else if (opacityRatio < 1) { // 如果源图片包含alpha值，且需降低当前透明度，则在源alpha值基础上降低透明度
                        Color color = new Color(argb);
                        int alpha = (int) (color.getAlpha() * opacityRatio);
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                        argb = color.getRGB();
                    }
                }
                // 在目标图片中的位置
                int targetX = x - beginX;
                int targetY = y - beginY;
                target.setRGB(targetX, targetY, argb);
            }
        }
        return target;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * 保存图片
     *
     * @param image     图片
     * @param dir       存储位置
     * @param filename  文件名
     * @param extension 后缀名
     * @throws IOException 系统没有写入权限
     */
    public static void save(BufferedImage image, String dir, String filename, String extension)
            throws IOException {
        FileOutputStream output = null;
        try {
            File file = new File(dir);
            if (!file.exists()) { // 因在实例化FileOutputStream时如果Dir未存在会报错,所以先保证dir有效
                file.mkdirs();
            }
            output = new FileOutputStream(
                    dir + IOUtil.FILE_SEPARATOR + filename + Strings.DOT + extension);
            ImageIO.write(image, extension, output);
            output.flush();
        } finally {
            try {
                if (output != null) { // 关闭输出流
                    output.close();
                }
            } catch (IOException e) { // 此处异常不需处理
            }
        }
    }

    /**
     * 保存图片
     *
     * @param bytes     图片字节数组
     * @param dir       存储位置
     * @param filename  文件名
     * @param extension 后缀名
     * @throws IOException 系统没有写入权限
     */
    public static void save(byte[] bytes, String dir, String filename, String extension)
            throws IOException {
        FileOutputStream output = null;
        try {
            File file = new File(dir);
            if (!file.exists()) { // 因在实例化FileOutputStream时如果Dir未存在会报错,所以先保证dir有效
                file.mkdirs();
            }
            output = new FileOutputStream(
                    dir + IOUtil.FILE_SEPARATOR + filename + Strings.DOT + extension);
            FileCopyUtils.copy(bytes, output);
        } finally {
            try {
                if (output != null) { // 关闭输出流
                    output.close();
                }
            } catch (IOException e) { // 此处异常不需处理
            }
        }
    }

    public static byte[] toBytes(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, os);
        return os.toByteArray();
    }

    private static void checkSize(Image image) {
        waitForLoading(image);
        int width = image.getWidth(null);
        if (width < 1) {
            throw new IllegalArgumentException("image width " + width + " is out of range");
        }
        int height = image.getHeight(null);
        if (height < 1) {
            throw new IllegalArgumentException("image height " + height + " is out of range");
        }
    }

    /**
     * 等待图片装载
     *
     * @param image 图片
     */
    private static void waitForLoading(Image image) {
        try {
            TRACKER.addImage(image, 0);
            TRACKER.waitForID(0);
            TRACKER.removeImage(image, 0);
        } catch (InterruptedException e) {
            LogUtil.error(IOUtil.class, e);
        }
    }

    /**
     * 缩放图片
     *
     * @param image 原图片
     * @param width 缩放目标宽度
     * @return 缩放后得到的图片
     * @author jianglei
     */
    public static BufferedImage zoom(Image image, int width) {
        int originalWidth = image.getWidth(null);
        if (originalWidth == width) {
            if (image instanceof BufferedImage) {
                return (BufferedImage) image;
            }
        }
        int originalHeight = image.getHeight(null);
        checkSize(image);

        // 计算等比高宽
        int height = -1;
        double scaleW = (double) originalWidth / (double) width;
        double scaleY = (double) originalHeight / (double) height;
        if (scaleW >= 0 && scaleY >= 0) {
            if (scaleW > scaleY) {
                height = -1;
            } else {
                width = -1;
            }
        }

        // 渲染缩略图
        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        checkSize(newImage);
        BufferedImage bi = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.drawImage(newImage, 0, 0, null);
        return bi;
    }

    /**
     * 缩放图片
     *
     * @param in    原图片输入流
     * @param width 缩放目标宽度
     * @return 缩放后得到的图片
     * @throws IOException 缩放异常(原文件损坏或指定缩放大小错误)
     */
    public static BufferedImage zoom(InputStream in, int width) throws IOException {
        try {
            byte[] bytes = IOUtils.toByteArray(in);// 将文件流转换为Byte数组
            Image originalImage = Toolkit.getDefaultToolkit().createImage(bytes);// 将Byte数组转换为图片
            waitForLoading(originalImage); // 等待图片加载
            return zoom(originalImage, width);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) { // 无需处理该异常
            }
        }
    }

    /**
     * 创建用RGB格式表示颜色值的纯色图片
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @param rgb    red-green-blue形式的十六进制颜色值，形如：0x020507
     * @return 创建的图片
     */
    public static BufferedImage createPureRgbImage(int width, int height, int rgb) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    /**
     * 创建用ARGB格式表示颜色值的纯色图片
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @param argb   alpha-red-green-blue形式的十六进制颜色值，形如：0xff020507，其中ff为alpha值
     * @return 创建的图片
     */
    public static BufferedImage createPureArgbImage(int width, int height, int argb) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, argb);
            }
        }
        return image;
    }

    /**
     * 创建纯色图片
     *
     * @param width   图片宽度
     * @param height  图片高度
     * @param rgb     red-green-blue形式的十六进制颜色值，形如：0x020507
     * @param opacity 不透明度，1-完全不透明，0-完全透明
     * @return 创建的图片
     */
    public static BufferedImage createPureColorImage(int width, int height, int rgb, float opacity) {
        int argb = rgbToArgb(rgb, toAlpha(opacity));
        return createPureArgbImage(width, height, argb);
    }

    /**
     * 不透明度转换为alpha值
     *
     * @param opacity 不透明度，1-完全不透明，0-完全透明
     * @return alpha值
     */
    public static int toAlpha(float opacity) {
        // 不透明度在[0,1]之间
        if (opacity < 0) {
            opacity = 0;
        } else if (opacity > 1) {
            opacity = 1;
        }
        return Math.round(opacity * 255);
    }

    public static int rgbToArgb(int rgb, int alpha) {
        Color color = new Color(rgb);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        return color.getRGB();
    }

    /**
     * 将指定源图片覆盖至指定目标图片上的指定位置，返回新的图片<br/>
     * 指定位置坐标如果不在目标图片内，则返回目标图片，没有变化；如果覆盖后超出目标图片范围，则超出的部分被裁剪忽略
     *
     * @param source 源图片
     * @param target 目标图片
     * @param x      源图片左上角位于目标图片中坐标的x轴
     * @param y      源图片左上角位于目标图片中坐标的y轴
     * @return 覆盖后生成的新图片
     */
    public static BufferedImage cover(BufferedImage source, BufferedImage target, int x, int y) {

        return null;
    }

}
