package cn.org.joinup.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
public class DelaySignDTO {
    private Long taskId;
    private Integer courseScheduleId;
}
