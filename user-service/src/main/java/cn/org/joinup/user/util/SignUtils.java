package cn.org.joinup.user.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class SignUtils {

    private final static String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成签名
     * @param params 参与签名的参数
     * @param appSecret 存储在服务端的密钥
     * @return 签名后的十六进制字符串
     */
    public static String generateSignature(Map<String, String> params, String appSecret) {
        try {
            // 1. 字典排序并拼接：key1=value1&key2=value2
            String baseString = new TreeMap<>(params).entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            // 2. 初始化 HmacSHA256
            Mac hmacSHA256 = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(
                    appSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            hmacSHA256.init(secretKey);

            // 3. 计算哈希并转为 Hex
            byte[] hashBytes = hmacSHA256.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            log.error("签名生成失败", e);
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateSignature(Map.of("appKey", "BUAA-ClassHopper-Android"), "a2901d1fg!62"));
    }
}