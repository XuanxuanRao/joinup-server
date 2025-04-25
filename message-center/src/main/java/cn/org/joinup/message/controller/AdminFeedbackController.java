package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.vo.FeedbackVO;
import cn.org.joinup.message.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/message/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final IFeedbackService feedbackService;

    @GetMapping("/list")
    public Result<PageResult<FeedbackVO>> query(@RequestParam(required = false) Boolean handled,
                                                @RequestParam(defaultValue = "1") int pageNum,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return feedbackService.pageQuery(handled, pageNum, pageSize);
    }

    @PostMapping("/{feedbackId}/handle")
    public Result<Void> handle(@PathVariable Long feedbackId) {
        return feedbackService.setHandled(feedbackId);
    }

}
