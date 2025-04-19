package cn.org.joinup.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.common.exception.SystemException;
import cn.org.joinup.user.domain.dto.*;
import cn.org.joinup.user.domain.po.Interest;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import cn.org.joinup.user.service.IUserInterestService;
import cn.org.joinup.user.service.IUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInterestService userInterestService;

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo() {
        User user = userService.getById(UserContext.getUser());
        if (user == null) {
            return Result.error("System error");
        }
        return Result.success(BeanUtil.copyProperties(user, UserDTO.class));
    }

    @PostMapping("/register")
    public Result<UserLoginVO> register(@Validated @RequestBody RegisterFormDTO registerFormDTO) {
        return Result.success(userService.register(registerFormDTO));
    }

    @GetMapping("/{id}")
    public Result<UserDTO> queryUser(@PathVariable Long id) {
        User user = userService.getById(id);
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
        User user = BeanUtil.copyProperties(updateUserDTO, User.class);
        user.setId(UserContext.getUser());
        user.setUpdateTime(LocalDateTime.now());
        System.out.println(user);
        if (!userService.updateById(user)) {
            return Result.error("更新用户信息失败，请稍后再试");
        }
        return Result.success();
    }

    @ApiOperation("获取用户的兴趣")
    @GetMapping("/interest/list")
    public Result<List<Interest>> getUserInterests() {
        return Result.success(userInterestService.getUserInterests(null));
    }

    @ApiOperation("添加用户兴趣")
    @PostMapping("/interest")
    public Result<Void> addUserInterest(@RequestParam Long interestId) {
        userInterestService.addUserInterest(interestId);
        return Result.success();
    }


}
