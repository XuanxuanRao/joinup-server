package cn.org.joinup.user.service;

import cn.org.joinup.user.domain.po.Interest;
import cn.org.joinup.user.domain.po.UserInterest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IUserInterestService extends IService<UserInterest> {
    UserInterest addUserInterest(Long interestId);

    List<Interest> getUserInterests(Long parentInterestId);
}
