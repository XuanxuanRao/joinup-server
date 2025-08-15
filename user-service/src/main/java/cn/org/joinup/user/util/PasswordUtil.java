package cn.org.joinup.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for encrypting and decrypting long integer IDs using AES encryption.
 */
@Component
public class PasswordUtil {
    @Value("${joinup.aes.key}")
    private String aesKeyFromConfig;

    private static String key;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @PostConstruct
    public void init() {
        key = aesKeyFromConfig;
    }

    /**
     * Encrypts a long integer ID using AES encryption.
     * @param raw  the content to encrypt
     * @return The encrypted content as a hexadecimal string
     */
    public static String encrypt(String raw) {
        // 创建 AES 密钥
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // 创建 Cipher 对象
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        // 初始化 Cipher 为加密模式
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        // 加密输入文本
        byte[] encryptedBytes;
        try {
            encryptedBytes = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        // 将加密字节转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : encryptedBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Decrypts a hexadecimal string using AES encryption.
     * @param hexInput The hexadecimal string to decrypt
     * @return The decrypted content as a string
     * @throws Exception If an error occurs during decryption
     */
    public static String decrypt(String hexInput) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // 将十六进制字符串转换为字节数组
        byte[] encryptedBytes = new byte[hexInput.length() / 2];
        for (int i = 0; i < hexInput.length(); i += 2) {
            encryptedBytes[i / 2] = (byte) ((Character.digit(hexInput.charAt(i), 16) << 4)
                    + Character.digit(hexInput.charAt(i + 1), 16));
        }

        // 解密输入字节
        return new String(cipher.doFinal(encryptedBytes), StandardCharsets.UTF_8);
    }
}
