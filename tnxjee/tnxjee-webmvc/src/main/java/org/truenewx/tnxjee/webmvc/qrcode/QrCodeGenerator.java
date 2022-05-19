package org.truenewx.tnxjee.webmvc.qrcode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.FileExtensions;
import org.truenewx.tnxjee.core.util.IOUtil;
import org.truenewx.tnxjee.core.util.ImageUtil;
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
    private static String EXTENSION = FileExtensions.PNG;

    private String root;

    public void setRoot(String root) {
        this.root = root;
    }

    public String generate(String value, int size, String logoUrl) throws IOException, WriterException {
        String md5 = EncryptUtil.encryptByMd5(value);
        File imageFile = getImageFile(md5);
        if (imageFile.exists()) { // 已经存在的文件不再重复生成
            return md5;
        }
        String dir = getDir(md5);
        // 产生二维码资源
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, Strings.ENCODING_UTF8);
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, size, size, hints);

        bitMatrix = updateBit(bitMatrix, 0);
        // 将二维码转换为BufferedImage
        BufferedImage image = toBufferedImage(bitMatrix);
        image = ImageUtil.zoom(image, size);
        // 载入logo
        if (StringUtils.isNotEmpty(logoUrl)) {
            Image logoImage;
            if (logoUrl.startsWith("http")) {
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

        // 验证文件夹是否存在
        File outputDir = new File(dir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 保存二维码图片
        ImageIO.write(image, EXTENSION, imageFile);
        return md5;
    }

    private String getDir(String md5) {
        Assert.hasText(this.root, "The root must has text");
        // 取MD5码的每两位一层目录，划分为三层目录，以控制每个目录的文件数量
        return this.root + IOUtil.FILE_SEPARATOR + md5.substring(0, 2) + IOUtil.FILE_SEPARATOR
                + md5.substring(2, 4) + IOUtil.FILE_SEPARATOR + md5.substring(4, 6)
                + IOUtil.FILE_SEPARATOR;
    }

    private File getImageFile(String dir, String md5) {
        return new File(dir + md5 + Strings.DOT + EXTENSION);
    }

    /**
     * 将二维码转换为BufferedImage
     *
     * @param matrix 二维码资源
     * @return
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

    private BitMatrix updateBit(BitMatrix matrix, int margin) {
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

    public File getImageFile(String md5) {
        String dir = getDir(md5);
        return getImageFile(dir, md5);
    }

}
