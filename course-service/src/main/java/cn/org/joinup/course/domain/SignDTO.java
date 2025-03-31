package cn.org.joinup.course.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
public class SignDTO {
    private String studentId;
    private Integer courseId;
}
