package cn.org.joinup.common.util;

public class RegexUtil {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
    public static final String USERNAME_REGEX = "^[\\u4e00-\\u9fa5\\w\\s]{2,15}$";
    public static final String CAPTCHA_REGEX = "^[0-9]{6}$";
    public static final String URL_REGEX = "^http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$";
    public static final String IP_REGEX = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$";


    public static boolean isEmailValid(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isPhoneValid(String phone) {
        return phone.matches(PHONE_REGEX);
    }

    public static boolean isPasswordValid(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public static boolean isUsernameValid(String username) {
        return username.matches(USERNAME_REGEX);
    }

}
