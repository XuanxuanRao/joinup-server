package cn.org.joinup.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.user.domain.dto.request.AdminUpdateAPPInfoRequestDTO;
import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.APPInfoVO;
import cn.org.joinup.user.mapper.APPInfoMapper;
import cn.org.joinup.user.service.IAPPInfoService;
import cn.org.joinup.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class APPInfoServiceImpl extends ServiceImpl<APPInfoMapper, APPInfo> implements IAPPInfoService {

    private final IUserService userService;

    @Override
    public Optional<APPInfo> getActiveAPPInfo(String appKey) {
        return Optional.ofNullable(lambdaQuery().eq(APPInfo::getAppKey, appKey)
                .eq(APPInfo::getEnabled, true)
                .eq(APPInfo::getDeleted, false)
                .one());
    }

    @Override
    public PageResult<APPInfoVO> list(Boolean enabled, Integer pageNum, Integer pageSize) {
        Page<APPInfo> page = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<APPInfo>()
                        .eq(enabled != null, APPInfo::getEnabled, enabled)
                        .eq(APPInfo::getDeleted, false)
                        .orderByDesc(APPInfo::getCreateTime));

        List<APPInfoVO> appInfoVOList = page.getRecords()
                .stream()
                .map(appInfo -> {
                    var vo = BeanUtil.copyProperties(appInfo, APPInfoVO.class);
                    vo.setUsersCount(userService.lambdaQuery()
                            .eq(User::getAppKey, appInfo.getAppKey())
                            .count());
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(new Page<APPInfoVO>()
                .setRecords(appInfoVOList)
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent()));
    }

    @Override
    public APPInfo updateAPPInfo(String appKey, AdminUpdateAPPInfoRequestDTO updateAPPInfoDTO) {
        var app = lambdaQuery()
                .eq(APPInfo::getAppKey, appKey)
                .eq(APPInfo::getDeleted, false)
                .one();

        if (app == null) {
            throw new IllegalArgumentException("appKey not found");
        }

        BeanUtil.copyProperties(updateAPPInfoDTO, app);
        updateById(app);
        app.setUpdateTime(LocalDateTime.now());
        return app;
    }

}
