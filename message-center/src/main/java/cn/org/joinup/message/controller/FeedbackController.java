package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.dto.AddFeedbackDTO;
import cn.org.joinup.message.service.IFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message/feedback")
@RequiredArgsConstructor
@Api(tags = "用户反馈接口")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    @ApiOperation("用户提交反馈")
    @PostMapping("/add")
    public Result<Void> submit(@Validated @RequestBody AddFeedbackDTO addFeedbackDTO) {
        return feedbackService.submit(addFeedbackDTO);
    }

}
