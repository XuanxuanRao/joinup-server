package cn.org.joinup.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.TeamClient;
import cn.org.joinup.api.dto.UserTeamStatisticDTO;
import cn.org.joinup.common.exception.SystemException;
import cn.org.joinup.user.domain.dto.*;
import cn.org.joinup.user.domain.dto.request.ThirdPartyAuthRequestDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.user.domain.vo.ThirdPartyAuthResponseVO;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import cn.org.joinup.user.service.AuthService;
import cn.org.joinup.user.service.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;
    private final TeamClient teamClient;
    private final AuthService authService;

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo() {
        User user = userService.getUserById(UserContext.getUser());
        if (user == null) {
            log.info("用户不存在");
            return Result.error("System error");
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        try {
            Result<UserTeamStatisticDTO> userTeamStatistic = teamClient.getMyTeamCount();
            BeanUtil.copyProperties(userTeamStatistic.getData(), userDTO);
        } catch (Exception e) {
            log.error("meet error while get user team count", e);
        }
        return Result.success(userDTO);
    }

    @PostMapping("/register")
    public Result<UserLoginVO> register(@Validated @RequestBody RegisterFormDTO registerFormDTO) {
        return Result.success(userService.register(registerFormDTO));
    }

    @GetMapping("/{id}")
    public Result<UserDTO> queryUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("User not found");
        }

        return Result.success(BeanUtil.copyProperties(user, UserDTO.class));
    }

    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO);
    }

    @ApiOperation("微信登录")
    @PostMapping("/wxLogin")
    public Result<UserLoginVO> wxLogin(@Validated @RequestBody WxLoginDTO wxLoginDTO) {
        try {
            return Result.success(userService.wxLogin(wxLoginDTO.getCode()));
        } catch (SystemException e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("微信注册")
    @PostMapping("/wxRegister")
    public Result<UserLoginVO> wxRegister(@Validated @RequestBody WxRegisterFormDTO wxRegisterFormDTO) {
        return userService.wxRegister(wxRegisterFormDTO);
    }

    @ApiOperation("账号密码登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@Validated @RequestBody LoginFormDTO loginFormDTO) {
        return Result.success(userService.login(loginFormDTO));
    }

    @ApiOperation("进行北航身份验证")
    @PostMapping("/verify")
    public Result<Void> verifyIdentity(@Validated @RequestBody VerifyIdentityDTO verifyIdentityDTO) {
        return userService.verifyIdentity(verifyIdentityDTO);
    }

    @ApiOperation("修改用户信息")
    @PutMapping
    public Result<Void> updateUser(@Validated @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUserInfo(updateUserDTO);
    }

    @ApiOperation("获取新的token")
    @GetMapping("/refreshToken")
    public Result<UserLoginVO> refreshToken() {
        return userService.refreshToken();
    }

    @ApiOperation("获取sso密码")
    @GetMapping("/ssoPassword")
    public Result<String> getSsoPassword() {
        return Result.success(userService.getSsoPassword());
    }

    @ApiOperation("第三方应用认证登录")
    @PostMapping("/third-auth")
    public Result<ThirdPartyAuthResponseVO> thirdPartyAuth(@Validated @RequestBody ThirdPartyAuthRequestDTO authRequestDTO) {
        log.info("Login request from {}, appUUID: {}", authRequestDTO.getAppKey(), authRequestDTO.getAppUUID());
        try {
            return Result.success(authService.thirdPartyAuth(authRequestDTO));
        } catch (Exception e) {
            return Result.error("认证失败: " + e.getMessage());
        }
    }
}
