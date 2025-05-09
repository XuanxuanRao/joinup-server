package cn.org.joinup.file.util;

import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;

/**
 * 文件工具类
 * @author chenxuanrao06@gmail.com
 */
public class FileUtil {
    /**
     * 计算File对象的MD5值
     * @param file 文件对象
     * @return MD5哈希值
     */
    public static String calculateMD5(MultipartFile file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(file.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("计算文件MD5失败", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
