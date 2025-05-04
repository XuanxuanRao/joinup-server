package cn.org.joinup.message.service;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.dto.AddFeedbackDTO;
import cn.org.joinup.message.domain.po.Feedback;
import cn.org.joinup.message.domain.vo.FeedbackVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IFeedbackService extends IService<Feedback> {

    Result<Void> submit(AddFeedbackDTO addFeedbackDTO);

    Result<PageResult<FeedbackVO>> pageQuery(Boolean handled, int pageNum, int pageSize);

    Result<Void> setHandled(Long feedbackId);
}
