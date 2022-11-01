package org.truenewx.tnxjee.core.util;

import java.io.*;
import java.security.*;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.crypto.Md5Encryptor;

/**
 * 加密工具类
 *
 * @author jianglei
 */
public class EncryptUtil {

    private static byte[] toBytes(Object source) {
        try {
            if (source instanceof File) {
                return FileUtils.readFileToByteArray((File) source);
            } else if (source instanceof InputStream) {
                return IOUtils.toByteArray((InputStream) source);
            } else if (source instanceof Reader) {
                return IOUtils.toByteArray((Reader) source, Strings.ENCODING_UTF8);
            } else if (source instanceof byte[]) {
                return (byte[]) source;
            } else {
                return source.toString().getBytes();
            }
        } catch (IOException e) {
            LogUtil.error(Md5Encryptor.class, e);
            return null;
        }
    }

    public static String encryptByMd5(Object source) {
        byte[] data = toBytes(source);
        return DigestUtils.md5Hex(data);
    }

    public static String encryptByMd5_16(Object source) {
        String s = encryptByMd5(source);
        return s.substring(8, 24);
    }

    public static String encryptByBase64(Object source) {
        if (source != null) {
            byte[] data = toBytes(source);
            return Base64.getEncoder().encodeToString(data).replaceAll("\n", "");
        }
        return null;
    }

    public static String decryptByBase64(String encryptedText) {
        if (encryptedText != null) {
            byte[] data = Base64.getDecoder().decode(encryptedText);
            if (data != null) {
                return new String(data);
            }
        }
        return null;
    }

    public static StringKeyPair generateKeyPair(String algorithm, int size) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(size);
        KeyPair keyPair = generator.genKeyPair();
        return new StringKeyPair(algorithm, keyPair);
    }

    public static String encryptByRsa(Object source, InputStream publicKey) {
        try {
            byte[] data = toBytes(source);
            /** 将文件中的公钥对象读出 */
            ObjectInputStream ois = new ObjectInputStream(publicKey);
            Key key = (Key) ois.readObject();
            ois.close();
            /** 得到Cipher对象来实现对源数据的RSA加密 */
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            /** 执行加密操作 */
            byte[] b1 = cipher.doFinal(data);
            return encryptByBase64(b1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptByRsa(String encryptedText, InputStream privateKey) {
        try {
            /** 将文件中的私钥对象读出 */
            ObjectInputStream ois = new ObjectInputStream(privateKey);
            Key key = (Key) ois.readObject();
            ois.close();
            /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return decryptByBase64(encryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptBySha1(Object source) {
        try {
            byte[] data = toBytes(source);
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data);
            byte digestBytes[] = digest.digest();
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < digestBytes.length; i++) {
                String shaHex = Integer.toHexString(digestBytes[i] & 0xFF);
                if (shaHex.length() < 2) {
                    s.append(0);
                }
                s.append(shaHex);
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
