package cn.org.joinup.common.constant;

/**
 * @author chenxuanrao06@gmail.com
 */
public class SystemConstant {
    /**
     * token 在请求头中的 key(名字)
     */
    public static final String TOKEN_NAME = "authorization";
    /**
     * 用户 id 在请求头中的 key(名字)
     */
    public static final String USER_ID_HEADER_NAME = "user-id";
    public static final String USER_ROLE_HEADER_NAME = "role";
    public static final String USER_ID_PAYLOAD_NAME = "userId";
    public static final String USER_ROLE_PAYLOAD_NAME = "role";
    /**
     * 允许上传文件的最大大小(20MB)
     */
    public static final Long MAX_UPLOAD_FILE_SIZE = 20 * 1024 * 1024L;
    /**
     * 允许上传图片的最大大小(5MB)
     */
    public static final Long MAX_UPLOAD_IMAGE_SIZE = 5 * 1024 * 1024L;
}
