package org.truenewx.tnxjee.core.util;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * 
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
    public static byte[] crop(InputStream in, String formatName, int x, int y, int width,
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

    public static BufferedImage crop(BufferedImage image, String formatName, int x, int y,
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
     * 保存图片
     *
     * @param image     图片
     * @param dirsPath  存储位置
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

}
