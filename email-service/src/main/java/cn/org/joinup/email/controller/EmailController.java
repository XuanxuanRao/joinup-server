package cn.org.joinup.email.controller;

import cn.org.joinup.common.result.PageQuery;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.email.domain.po.EmailLog;
import cn.org.joinup.email.service.IEmailService;
import cn.org.joinup.api.dto.SendEmailDTO;
import cn.org.joinup.common.result.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/common/email")
public class EmailController {

    @Resource
    private IEmailService emailService;

    @PostMapping("/send")
    public Result<Void> sendEmail(@RequestBody SendEmailDTO sendEmailDTO) {
        if (sendEmailDTO.getAttachments() == null || sendEmailDTO.getAttachments().isEmpty()) {
            emailService.sendSimpleEmail(sendEmailDTO.getTo(), sendEmailDTO.getSubject(), sendEmailDTO.getBody());
        } else {
            emailService.sendEmailWithAttachments(sendEmailDTO.getTo(), sendEmailDTO.getSubject(), sendEmailDTO.getBody(), sendEmailDTO.getAttachments());
        }
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<EmailLog>> listEmailLogs(PageQuery query) {
        Page<EmailLog> page = emailService.page(query.toMpPage("create_time", false));
        return Result.success(PageResult.of(page, EmailLog.class));
    }
}
