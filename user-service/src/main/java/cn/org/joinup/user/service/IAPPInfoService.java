package cn.org.joinup.user.service;

import cn.org.joinup.user.domain.po.APPInfo;

import java.util.Optional;

public interface IAPPInfoService {
    Optional<APPInfo> getActiveAPPInfo(String appKey);
}
