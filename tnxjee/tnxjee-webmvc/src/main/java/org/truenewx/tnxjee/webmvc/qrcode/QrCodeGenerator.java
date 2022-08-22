package org.truenewx.tnxjee.webmvc.qrcode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.*;
import org.truenewx.tnxjee.web.context.SpringWebContext;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码生成器
 *
 * @author liuzhiyi
 */
@Component
@ConfigurationProperties("tnxjee.web.qrcode")
public class QrCodeGenerator {

    /**
     * 二维码图片扩展名
     */
    public static final String EXTENSION = FileExtensions.PNG;

    private String root;

    public void setRoot(String root) {
        this.root = root;
    }

    public File getImageFileByName(String name) {
        Assert.hasText(this.root, "The root must has text");
        // 取名称的每两位一层目录，划分为三层目录，以控制每个目录的文件数量
        String dir = this.root + IOUtil.FILE_SEPARATOR + name.substring(0, 2) + IOUtil.FILE_SEPARATOR
                + name.substring(2, 4) + IOUtil.FILE_SEPARATOR + name.substring(4, 6)
                + IOUtil.FILE_SEPARATOR;
        return new File(dir + name);
    }

    private File getImageFileByValue(String value) {
        String md5 = EncryptUtil.encryptByMd5(value);
        return getImageFileByName(md5 + Strings.DOT + EXTENSION);
    }

    public InputStream getInputStream(String value, int size, String logoUrl, int margin)
            throws IOException, WriterException {
        File imageFile = getImageFileByValue(value);
        if (imageFile.exists()) { // 已经存在的文件不再重复生成
            return new FileInputStream(imageFile);
        }
        BufferedImage image = createImage(value, size, logoUrl, margin);
        // 不缓存的二维码图片直接转换为二进制数组输入流
        return ImageUtil.toByteArrayInputStream(image, EXTENSION);
    }

    private BufferedImage createImage(String value, int size, String logoUrl, int margin)
            throws WriterException, IOException {
        // 产生二维码资源
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, Strings.ENCODING_UTF8);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, size, size, hints);

        bitMatrix = fillMargin(bitMatrix, margin);
        // 将二维码转换为BufferedImage
        BufferedImage image = toBufferedImage(bitMatrix);
        image = ImageUtil.zoom(image, size);
        // 载入logo
        if (StringUtils.isNotEmpty(logoUrl)) {
            Image logoImage;
            if (NetUtil.isHttpUrl(logoUrl, true)) {
                URL url = new URL(logoUrl);
                InputStream is = url.openConnection().getInputStream();
                logoImage = ImageIO.read(is);
            } else {
                String logoPath = SpringWebContext.getServletContext().getRealPath(logoUrl);
                logoImage = ImageIO.read(new File(logoPath));
            }
            Graphics2D gs = image.createGraphics();
            int logoWidth = logoImage.getWidth(null);
            int logoHeight = logoImage.getHeight(null);
            int logoX = (size - logoWidth) / 2;
            int logoY = (size - logoHeight) / 2;
            gs.drawImage(logoImage, logoX, logoY, null);
            gs.dispose();
            logoImage.flush();
        }
        return image;
    }

    private BitMatrix fillMargin(BitMatrix matrix, int margin) {
        if (margin > 0) {
            int tempM = margin * 2;
            int[] rec = matrix.getEnclosingRectangle(); // 获取二维码图案的属性
            int resWidth = rec[2] + tempM;
            int resHeight = rec[3] + tempM;
            BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
            resMatrix.clear();
            for (int i = margin; i < resWidth - margin; i++) { // 循环，将二维码图案绘制到新的bitMatrix中
                for (int j = margin; j < resHeight - margin; j++) {
                    if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                        resMatrix.set(i, j);
                    }
                }
            }
            return resMatrix;
        }
        return matrix;
    }

    /**
     * 将二维码转换为BufferedImage
     *
     * @param matrix 二维码资源
     * @return 图片对象
     * @author liuzhiyi
     */
    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    public String save(String value, int size, String logoUrl, int margin) throws IOException, WriterException {
        File imageFile = getImageFileByValue(value);
        String imageFileName = imageFile.getName();
        if (imageFile.exists()) { // 已经存在的文件不再重复生成
            return imageFileName;
        }

        BufferedImage image = createImage(value, size, logoUrl, margin);
        // 确保文件夹存在
        File outputDir = imageFile.getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        // 保存二维码图片
        ImageIO.write(image, EXTENSION, imageFile);
        return imageFileName;
    }

}
