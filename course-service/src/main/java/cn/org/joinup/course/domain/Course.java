package cn.org.joinup.course.domain;

import cn.org.joinup.course.enums.SignStatus;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class Course {
    private Integer id;
    private Integer courseId;
    private String courseName;
    private String courseType;
    private String weekDay;
    private String courseNum;
    private String teacherName;
    private String classroomName;
    private SignStatus signStatus;
    private LocalDateTime classBeginTime;
    private LocalDateTime classEndTime;
}

