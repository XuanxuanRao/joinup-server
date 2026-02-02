package cn.org.joinup.message.interfaces.controller.user;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.application.splash.dto.UserFetchSplashRequestDTO;
import cn.org.joinup.message.interfaces.vo.SplashUserVO;
import cn.org.joinup.message.application.splash.service.impl.SplashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/message/splash")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserSplashController {

    private final SplashService splashService;

    @GetMapping("/fetch")
    public Result<SplashUserVO> fetchSplash(@Validated UserFetchSplashRequestDTO fetchSplashDTO) {
        try {
            return Result.success(splashService.fetchSplash(fetchSplashDTO));
        } catch (Exception e) {
            log.error("fetch splash error", e);
            return Result.error("fetch splash error");
        }
    }

}
