package cn.org.joinup.api.dto;

import cn.org.joinup.common.enums.Gender;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class UserDTO {
    private String username;
    private String avatar;
    private String createTime;
    private String studentId;
    private Gender gender;
}
