package cn.org.joinup.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {
    private String appId;
    private String appSecret;
    private String msgDataFormat;
}
