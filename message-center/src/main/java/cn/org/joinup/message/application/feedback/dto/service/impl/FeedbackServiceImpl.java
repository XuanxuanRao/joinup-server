package cn.org.joinup.message.application.feedback.dto.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.message.application.feedback.dto.AddFeedbackDTO;
import cn.org.joinup.message.domain.feedback.entity.Feedback;
import cn.org.joinup.message.interfaces.vo.FeedbackVO;
import cn.org.joinup.message.infrastructure.repository.FeedbackMapper;
import cn.org.joinup.message.application.feedback.dto.service.IFeedbackService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

    private final UserClient userClient;

    @Override
    public Result<Void> submit(AddFeedbackDTO addFeedbackDTO) {
        Feedback feedback = BeanUtil.copyProperties(addFeedbackDTO, Feedback.class);
        feedback.setUserId(UserContext.getUserId());
        feedback.setHandled(false);
        feedback.setCreateTime(LocalDateTime.now());
        save(feedback);
        return Result.success();
    }

    @Override
    public Result<PageResult<FeedbackVO>> pageQuery(Boolean handled, int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, 1);
        if (pageSize <= 0) {
            pageSize = 5;
        } else if (pageSize > 20) {
            pageSize = 20;
        }


        Page<Feedback> page = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Feedback>()
                        .eq(handled != null, Feedback::getHandled, handled)
                        .orderByDesc(Feedback::getCreateTime));

        List<FeedbackVO> collect = page.getRecords()
                .stream()
                .map(feedback -> {
                    FeedbackVO feedbackVO = BeanUtil.copyProperties(feedback, FeedbackVO.class);
                    UserDTO userInfo = userClient.queryUser(feedback.getUserId()).getData();
                    feedbackVO.setUsername(userInfo.getUsername());
                    feedbackVO.setAvatar(userInfo.getAvatar());
                    return feedbackVO;
                })
                .collect(Collectors.toList());

        return Result.success(PageResult.of(new Page<FeedbackVO>()
                .setRecords(collect)
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent())));
    }

    @Override
    public Result<Void> setHandled(Long feedbackId) {
        Feedback feedback = getById(feedbackId);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }

        feedback.setHandled(true);

        updateById(feedback);

        return Result.success();
    }

}
