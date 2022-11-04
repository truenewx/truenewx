package org.truenewx.tnxjee.webmvc.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.jwt.JwtConstants;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.core.util.EncryptUtil;
import org.truenewx.tnxjee.core.util.JacksonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * JWT解析器实现
 *
 * @author jianglei
 */
public class JwtParserImpl implements JwtParser, ContextInitializedBean {

    private KeyFactory rsaKeyFactory;
    private Map<String, JwtDecryption> decryptions = new HashMap<>();

    public JwtParserImpl() {
        try {
            this.rsaKeyFactory = KeyFactory.getInstance(JwtConstants.ASYMMETRIC_ALGORITHM_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, JwtDecryption> beans = context.getBeansOfType(JwtDecryption.class);
        for (JwtDecryption decryption : beans.values()) {
            String encryptionName = decryption.getEncryptionName();
            if (this.decryptions.put(encryptionName, decryption) != null) {
                throw new Exception("An decryption with encryption's name '" + encryptionName + "' already exists");
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return this.decryptions.size() > 0;
    }

    @Override
    public <T> T parse(String type, String jwt, Class<T> expectedType) {
        if (isAvailable() && jwt != null && jwt.startsWith(JwtConstants.JWT_PREFIX)) {
            String[] array = jwt.substring(JwtConstants.JWT_PREFIX.length()).split(Strings.SLASH, 3);
            if (array.length == 3) {
                if (StringUtils.isBlank(type)) {
                    type = Strings.EMPTY;
                }
                String encryptionName = array[0];
                String payload = array[1];
                String token = array[2];

                JWTVerifier verifier = getVerifier(type, encryptionName, payload);
                if (verifier != null) {
                    try {
                        List<String> audience = verifier.verify(token).getAudience();
                        String audienceJson = CollectionUtil.getFirst(audience, null);
                        if (StringUtils.isNotBlank(audienceJson)) {
                            return JacksonUtil.CLASSED_MAPPER.readValue(audienceJson, expectedType);
                        }
                    } catch (Exception e) {
                        LogUtil.error(getClass(), e);
                    }
                }
            }
        }
        return null;
    }

    private JWTVerifier getVerifier(String type, String encryptionName, String payload) {
        Algorithm algorithm = getAlgorithm(type, encryptionName, payload);
        if (algorithm != null) {
            return JWT.require(algorithm).build();
        }
        return null;
    }

    protected Algorithm getAlgorithm(String type, String encryptionName, String payload) {
        JwtDecryption decryption = this.decryptions.get(encryptionName);
        if (decryption != null) {
            String secretKey = decryption.getDecryptSecretKey(type, payload);
            if (secretKey != null) {
                if (decryption.isSymmetric(type)) {
                    return Algorithm.HMAC256(secretKey);
                } else {
                    RSAPublicKey publicKey = EncryptUtil.generatePublic(this.rsaKeyFactory, secretKey);
                    return Algorithm.RSA256(publicKey, null);
                }
            }
        }
        return null;
    }

}
