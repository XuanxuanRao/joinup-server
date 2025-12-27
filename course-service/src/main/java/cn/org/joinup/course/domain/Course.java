package cn.org.joinup.course.domain;

import cn.org.joinup.course.enums.SignStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime classBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime classEndTime;
}

