package cn.org.joinup.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
@Data
@Builder
public class SendEmailDTO {
    private String to;
    private String subject;
    private String body;
    private List<File> attachments;
}
