package cn.org.joinup.message.application.feedback.dto.service;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.application.feedback.dto.AddFeedbackDTO;
import cn.org.joinup.message.domain.feedback.entity.Feedback;
import cn.org.joinup.message.interfaces.vo.FeedbackVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IFeedbackService extends IService<Feedback> {

    Result<Void> submit(AddFeedbackDTO addFeedbackDTO);

    Result<PageResult<FeedbackVO>> pageQuery(Boolean handled, int pageNum, int pageSize);

    Result<Void> setHandled(Long feedbackId);
}
