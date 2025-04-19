package cn.org.joinup.user.service;

import cn.org.joinup.user.domain.po.Interest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

public interface IInterestService extends IService<Interest> {
    Interest getInterestById(Serializable id);

    /**
     * 获取所有的interest标签列表
     * @return interest列表
     */
    List<Interest> getInterests();

    /**
     * 查询某个标签下的interest标签列表（一层）
     * @param parentId 要查询的标签id
     * @return interest列表
     */
    List<Interest> getInterests(Long parentId);

    Boolean isLeaf(Long id);
}
