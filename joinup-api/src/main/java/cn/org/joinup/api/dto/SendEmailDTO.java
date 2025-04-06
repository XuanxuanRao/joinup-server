package cn.org.joinup.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDTO {
    private String to;
    private String subject;
    private String body;
    private List<File> attachments;
}
