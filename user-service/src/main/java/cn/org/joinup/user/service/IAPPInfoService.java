package cn.org.joinup.user.service;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.user.domain.dto.request.AdminUpdateAPPInfoRequestDTO;
import cn.org.joinup.user.domain.po.APPInfo;
import cn.org.joinup.user.domain.vo.APPInfoVO;

import java.util.Optional;

public interface IAPPInfoService {
    Optional<APPInfo> getActiveAPPInfo(String appKey);

    PageResult<APPInfoVO> list(Boolean enabled, Integer pageNum, Integer pageSize);

    APPInfo updateAPPInfo(String appKey, AdminUpdateAPPInfoRequestDTO updateAPPInfoDTO);
}
