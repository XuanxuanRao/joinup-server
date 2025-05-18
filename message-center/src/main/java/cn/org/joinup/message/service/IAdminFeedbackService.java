package cn.org.joinup.message.service;

import cn.org.joinup.message.domain.po.Feedback;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminFeedbackService extends IService<Feedback> {
    /**
     * 分页查询标签
     *
     * @param pageable 分页参数
     * @return 标签列表
     */
    IPage<Feedback> getPageFeedbacks(Pageable pageable);

    /**
     * 分页查询标签
     *
     * @param name     标签名称
     * @param pageable 分页参数
     * @return 标签列表
     */
    IPage<Feedback> getPageFeedbacksSearch(String name, Pageable pageable);
}
