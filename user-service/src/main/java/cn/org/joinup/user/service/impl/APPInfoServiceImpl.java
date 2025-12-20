package cn.org.joinup.user.service.impl;

import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.mapper.APPInfoMapper;
import cn.org.joinup.user.service.IAPPInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Service
public class APPInfoServiceImpl extends ServiceImpl<APPInfoMapper, APPInfo> implements IAPPInfoService {

    @Override
    public Optional<APPInfo> getActiveAPPInfo(String appKey) {
        return Optional.of(lambdaQuery().eq(APPInfo::getAppKey, appKey)
                .eq(APPInfo::getEnabled, true)
                .eq(APPInfo::getDeleted, false)
                .one());
    }

}
