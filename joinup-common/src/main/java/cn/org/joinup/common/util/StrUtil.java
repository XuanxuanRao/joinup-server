package cn.org.joinup.common.util;

/**
 * @author chenxuanrao06@gmail.com
 */
public class StrUtil {
    public static int getLevenshteinDistance(String str1, String str2) {
        if (str1 == null || str2 == null) {
            throw new IllegalArgumentException("Strings cannot be null");
        }

        if (str1.isEmpty()) {
            return str2.length();
        }
        if (str2.isEmpty()) {
            return str1.length();
        }

        // 确保 str1 是较短的字符串，以优化空间
        if (str1.length() > str2.length()) {
            String temp = str1;
            str1 = str2;
            str2 = temp;
        }

        int[] dp = new int[str1.length() + 1];

        // 初始化第一行 (当 str2 的长度为 0 时)
        for (int i = 0; i <= str1.length(); i++) {
            dp[i] = i;
        }

        for (int j = 1; j <= str2.length(); j++) {
            int previousDiagonal = dp[0]; // 存储上一行对角线的值
            dp[0] = j; // 当前行的第一个值为 j (str1 为空时)

            for (int i = 1; i <= str1.length(); i++) {
                int temp = dp[i]; // 存储当前的 dp[i]，用于下一次对角线更新
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i] = previousDiagonal;
                } else {
                    dp[i] = 1 + Math.min(dp[i - 1],     // Deletion
                            Math.min(dp[i],     // Insertion (实际上是上一行的当前列)
                                    previousDiagonal)); // Substitution
                }
                previousDiagonal = temp; // 更新对角线的值
            }
        }

        return dp[str1.length()];
    }

}
