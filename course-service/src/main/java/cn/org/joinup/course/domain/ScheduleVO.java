package cn.org.joinup.course.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class ScheduleVO {
    @JsonProperty("STATUS")
    private String status;
    private String total;
    private List<Course> result;
}
