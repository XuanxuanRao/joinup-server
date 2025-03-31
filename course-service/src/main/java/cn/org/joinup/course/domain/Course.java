package cn.org.joinup.course.domain;

import lombok.Data;


/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class Course {
    private Integer id;
    private String uuid;
    private Integer courseId;
    private String courseName;
    private String courseType;
    private String weekDay;
    private String courseNum;
    private String semesterId;
    private String semesterName;
    private String teacherId;
    private String teacherName;
    private String teacherPicUrl;
    private String teacherAcademy;
    private String classroomId;
    private String classroomUuid;
    private String classroomName;
    private String classroomLongitude;
    private String classroomLatitude;
    private String teachBuildId;
    private String teachBuildUuid;
    private String teachBuildName;
    private String storeyId;
    private String storeyName;
    private String teachTime;
    private String signStatus;
    private String classBeginTime;
    private String evaluateScore;
    private String evaluateStatus;
    private String signAssistantId;
    private String cloudMeetingRoomId;
    private String assistantTeaName;
    private String assistantStuName;
    private String courseSchedType;
    private String classEndTime;
}

