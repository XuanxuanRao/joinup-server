package cn.org.joinup.user.domain.vo;

import cn.org.joinup.common.enums.Gender;
import cn.org.joinup.user.enums.UserType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("管理员查看的用户信息")
public class AdminUserInfoVO {
    private Long id;
    private String username;
    private String avatar;
    private String email;
    private String studentId;
    private Boolean verified;
    private LocalDateTime createTime;
    private String appKey;
    private Gender gender;
    private UserType userType;
}
