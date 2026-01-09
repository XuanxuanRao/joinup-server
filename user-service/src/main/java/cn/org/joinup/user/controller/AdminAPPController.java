package cn.org.joinup.user.controller;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.request.AdminUpdateAPPInfoRequestDTO;
import cn.org.joinup.user.domain.vo.APPInfoVO;
import cn.org.joinup.user.service.IAPPInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/user/app")
@RequiredArgsConstructor
@Slf4j
public class AdminAPPController {

    private final IAPPInfoService appInfoService;

    @GetMapping("/list")
    public Result<PageResult<APPInfoVO>> list(@RequestParam(defaultValue = "0") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(appInfoService.list(null, pageNum, pageSize));
    }

    @PutMapping("/{appKey}")
    public Result<Void> updateAppInfo(@PathVariable String appKey,
                                      @Validated @RequestBody AdminUpdateAPPInfoRequestDTO updateAPPInfoDTO) {
        try {
            log.info("update app info, appKey: {}, updateAPPInfoDTO: {}", appKey, updateAPPInfoDTO);
            appInfoService.updateAPPInfo(appKey, updateAPPInfoDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("update app info failed, appKey: {}, updateAPPInfoDTO: {}", appKey, updateAPPInfoDTO, e);
            return Result.error(e.getMessage());
        }
    }

}
