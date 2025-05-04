package cn.org.joinup.team.serivice;

import cn.org.joinup.team.domain.po.Tag;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminTagService extends IService<Tag> {
    /**
     * 分页查询标签
     *
     * @param pageable 分页参数
     * @return 标签列表
     */
    IPage<Tag> getPageTags(Pageable pageable);

    /**
     * 分页查询标签
     *
     * @param name     标签名称
     * @param pageable 分页参数
     * @return 标签列表
     */
    IPage<Tag> getPageTagsSearch(String name, Pageable pageable);
}
