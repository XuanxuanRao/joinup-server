package cn.org.joinup.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@AllArgsConstructor
public class CourseQueryDTO {
    private String studentId;
    private LocalDate date;
}
