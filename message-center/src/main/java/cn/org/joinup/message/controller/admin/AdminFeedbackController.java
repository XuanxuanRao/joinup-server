package cn.org.joinup.message.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.dto.UpdateFeedbackDTO;
import cn.org.joinup.message.domain.po.Feedback;
import cn.org.joinup.message.service.IAdminFeedbackService;
import cn.org.joinup.message.service.IFeedbackService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/admin/message/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final IFeedbackService feedbackService;
    private final IAdminFeedbackService iAdminFeedbackService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminFeedbackService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<Feedback> list(Pageable pageable) {
        return iAdminFeedbackService.getPageFeedbacks(pageable);
    }

    // 更新反馈
    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateFeedbackDTO updateFeedbackDTO) {
        Feedback feedback = BeanUtil.copyProperties(updateFeedbackDTO, Feedback.class);
        feedback.setId(id);
        iAdminFeedbackService.updateById(feedback);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminFeedbackService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminFeedbackService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping("/querySearch")
    public Result<List<Feedback>> querySearch(@RequestParam String name) {
        List<Feedback> feedbacks = iAdminFeedbackService.list();
        List<Feedback> result = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            if (feedback.getContent().contains(name)) {
                result.add(feedback);
            }
        }

        return Result.success(result);
    }

    // 返回模糊查询数量
    @GetMapping("/searchCount")
    public Result<Long> searchCount(@RequestParam String name) {
        List<Feedback> feedbacks = iAdminFeedbackService.list();
        Long count = 0L;
        for (Feedback feedback : feedbacks) {
            if (feedback.getContent().contains(name)) {
                count++;
            }
        }
        return Result.success(count);
    }

    // 分页显示模糊查询结果
    @GetMapping("/search")
    public IPage<Feedback> search(@RequestParam String name,
                                      @RequestParam("page") int page, @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return iAdminFeedbackService.getPageFeedbacksSearch(name, pageable);
    }

    @PostMapping("/{feedbackId}/handle")
    public Result<Void> handle(@PathVariable Long feedbackId) {
        return feedbackService.setHandled(feedbackId);
    }

}
