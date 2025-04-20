package cn.org.joinup.user.service.impl;

import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.user.domain.po.Interest;
import cn.org.joinup.user.domain.po.UserInterest;
import cn.org.joinup.user.mapper.UserInterestMapper;
import cn.org.joinup.user.service.IInterestService;
import cn.org.joinup.user.service.IUserInterestService;
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
public class UserInterestServiceImpl extends ServiceImpl<UserInterestMapper, UserInterest> implements IUserInterestService {

    private final IInterestService interestService;

    @Override
    public UserInterest addUserInterest(Long interestId) {
        if (!interestService.isLeaf(interestId)) {
            throw new BadRequestException("Illegal Operation: Try to add a non-leaf interest node!");
        }

        UserInterest userInterest = new UserInterest();
        userInterest.setInterestId(interestId);
        userInterest.setUserId(UserContext.getUser());
        userInterest.setCreateTime(LocalDateTime.now());

        save(userInterest);

        return userInterest;
    }

    @Override
    public List<Interest> getUserInterests(Long parentInterestId) {
        return lambdaQuery()
                .eq(UserInterest::getUserId, UserContext.getUser())
                .eq(parentInterestId != null, UserInterest::getInterestId, parentInterestId)
                .list()
                .stream()
                .map(userInterest -> interestService.getById(userInterest.getInterestId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserInterest(Long interestId) {
        if (!interestService.isLeaf(interestId)) {
            throw new BadRequestException("Illegal Operation: Try to delete a non-leaf interest node!");
        }

        remove(lambdaQuery()
                .eq(UserInterest::getUserId, UserContext.getUser())
                .eq(UserInterest::getInterestId, interestId)
                .last("LIMIT 1"));
    }
}
