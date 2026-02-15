package cn.org.joinup.user.domain.po;

import cn.org.joinup.common.enums.Gender;
import cn.org.joinup.user.enums.UserType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;
    private String password;
    /**
     * 北航统一认证密码
     */
    private String ssoPassword;
    private String email;
    /**
     * 账号创建时间
     */
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String avatar;
    /**
     * 是否完成了校内身份验证
     */
    private Boolean verified;
    /**
     * 学号
     */
    private String studentId;
    private String openid;
    private String githubId;
    private Gender gender;

    /**
     * 用户角色：ADMIN-管理员，USER-普通用户
     */
    private String role;

    /**
     * 第三方应用标识
     */
    private String appKey;

    /**
     * 第三方用户唯一标识
     */
    @TableField("app_uuid")
    private String appUUID;

    @TableField("user_type")
    private UserType userType;
}
