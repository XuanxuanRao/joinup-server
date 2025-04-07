package cn.org.joinup.course.domain.vo;

import cn.org.joinup.course.domain.Course;
import lombok.Data;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class ScheduleVO {
    private String status;
    private String total;
    private List<Course> result;
}
