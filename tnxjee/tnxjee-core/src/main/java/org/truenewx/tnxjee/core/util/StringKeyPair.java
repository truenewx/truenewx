package org.truenewx.tnxjee.core.util;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 字符串型密钥对
 *
 * @author jianglei
 */
public class StringKeyPair {

    private KeyFactory factory;
    private String publicKey;
    private String privateKey;

    public StringKeyPair(String algorithm, String publicKey, String privateKey) throws NoSuchAlgorithmException {
        this.factory = KeyFactory.getInstance(algorithm);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public StringKeyPair(String algorithm, KeyPair keyPair) throws NoSuchAlgorithmException {
        this.factory = KeyFactory.getInstance(algorithm);
        Base64.Encoder base64Encoder = Base64.getEncoder();
        this.publicKey = base64Encoder.encodeToString(keyPair.getPublic().getEncoded());
        this.privateKey = base64Encoder.encodeToString(keyPair.getPrivate().getEncoded());
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    @JsonIgnore
    public PublicKey getPublic() {
        try {
            byte[] data = Base64.getDecoder().decode(this.publicKey);
            return this.factory.generatePublic(new X509EncodedKeySpec(data));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public PrivateKey getPrivate() {
        try {
            byte[] data = Base64.getDecoder().decode(this.privateKey);
            return this.factory.generatePrivate(new PKCS8EncodedKeySpec(data));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
