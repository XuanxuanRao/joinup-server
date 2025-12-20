package cn.org.joinup.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.joinup.common.exception.SystemException;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.config.UserRegisterProperties;
import cn.org.joinup.user.util.PasswordUtil;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.user.domain.dto.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.org.joinup.common.constant.RedisConstant;
import cn.org.joinup.common.exception.BadRequestException;
import cn.org.joinup.user.config.JwtProperties;
import cn.org.joinup.user.util.JwtTool;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.service.IUserService;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    private final StringRedisTemplate stringRedisTemplate;

    private final WxMaService wxMaService;

    private final SensitiveWordBs sensitiveWordBs;

    private final UserRegisterProperties userRegisterProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {

        User user = lambdaQuery()
                .eq(User::getUsername, loginDTO.getUsername())
                .one();


        if (user == null) {
            throw new BadRequestException("用户不存在");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }

        String token = jwtTool.createToken(user.getId(), user.getRole(), jwtProperties.getTokenTTL());
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        userLoginVO.setToken(token);
        return userLoginVO;
    }

    @Override
    @Transactional
    public UserLoginVO wxLogin(String code) throws SystemException {
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
            log.info("openid: {}, session_key: {}", sessionInfo.getOpenid(), sessionInfo.getSessionKey());

            User user = lambdaQuery()
                    .eq(User::getOpenid, sessionInfo.getOpenid())
                    .one();
            boolean isNewUser = user == null;
            // 如果用户不存在则注册
            if (user == null) {
                if (!userRegisterProperties.isWxEnabled()) {
                    throw new BadRequestException("Registration via WeChat is not allowed");
                }
                user = new User();
                user.setUsername("wx_" + sessionInfo.getOpenid().substring(0, 8));
                user.setOpenid(sessionInfo.getOpenid());
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                save(user);
            }
            String token = jwtTool.createToken(user.getId(), user.getRole(), jwtProperties.getTokenTTL());
            // 返回登录结果
            UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
            userLoginVO.setToken(token);
            userLoginVO.setNewUser(isNewUser);
            return userLoginVO;
        } catch (WxErrorException e) {
            log.error("微信登录失败: {}", e.getLocalizedMessage());
            throw new SystemException("微信登录失败");
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    @Override
    @Transactional
    public UserLoginVO register(RegisterFormDTO registerDTO) {
        if (!userRegisterProperties.isEmailEnabled()) {
            throw new BadRequestException("Registration via email is not allowed");
        }

        User user = lambdaQuery()
                .eq(User::getUsername, registerDTO.getUsername())
                .one();

        if (user != null) {
            throw new BadRequestException("用户名已存在");
        }

        String correctCode = stringRedisTemplate.opsForValue().get(RedisConstant.VERIFY_CODE_PREFIX + registerDTO.getEmail());
        if (!registerDTO.getVerifyCode().equals(correctCode)) {
                throw new BadRequestException("验证码错误");
        }

        user = BeanUtil.copyProperties(registerDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        if (!save(user)) {
            return null;
        }
        stringRedisTemplate.delete(RedisConstant.VERIFY_CODE_PREFIX + registerDTO.getEmail());

        String token = jwtTool.createToken(user.getId(), user.getRole(), jwtProperties.getTokenTTL());
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        userLoginVO.setToken(token);
        return userLoginVO;
    }

    @Override
    public Result<UserLoginVO> wxRegister(WxRegisterFormDTO wxRegisterFormDTO) {
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(wxRegisterFormDTO.getCode());
            log.info("openid: {}", sessionInfo.getOpenid());
            log.info("session_key: {}", sessionInfo.getSessionKey());

            User user = lambdaQuery()
                    .eq(User::getOpenid, sessionInfo.getOpenid())
                    .one();

            if (user != null) {
                return Result.error("用户已存在");
            }

            if (sensitiveWordBs.contains(wxRegisterFormDTO.getUsername())) {
                return Result.error("用户名包含敏感词");
            }

            user = new User();
            user.setUsername(StrUtil.isBlank(wxRegisterFormDTO.getUsername()) ? "wx_" + sessionInfo.getOpenid().substring(0, 8) : wxRegisterFormDTO.getUsername());
            user.setGender(wxRegisterFormDTO.getGender());
            user.setOpenid(sessionInfo.getOpenid());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            save(user);

            String token = jwtTool.createToken(user.getId(), user.getRole(), jwtProperties.getTokenTTL());
            // 返回登录结果
            UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
            userLoginVO.setToken(token);
            userLoginVO.setNewUser(true);
            return Result.success(userLoginVO);
        } catch (WxErrorException e) {
            return Result.error("微信登录失败");
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    @Override
    public Result<Void> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = lambdaQuery()
                .eq(User::getEmail, resetPasswordDTO.getEmail())
                .one();

        if (user == null) {
            throw new BadRequestException("用户不存在");
        }

        String correctCode = stringRedisTemplate.opsForValue().get(RedisConstant.VERIFY_CODE_PREFIX + resetPasswordDTO.getEmail());
        if (!resetPasswordDTO.getVerifyCode().equals(correctCode)) {
            throw new BadRequestException("验证码错误");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        user.setUpdateTime(LocalDateTime.now());
        if (!updateById(user)) {
            return Result.error("修改密码失败");
        }
        stringRedisTemplate.delete(RedisConstant.VERIFY_CODE_PREFIX + resetPasswordDTO.getEmail());

        final String key = cn.org.joinup.user.constant.RedisConstant.USER_INFO_KEY_PREFIX + user.getId();
        stringRedisTemplate.delete(key);

        return Result.success();
    }

    @Override
    public Result<Void> verifyIdentity(VerifyIdentityDTO verifyIdentityDTO) {
        Long userId = UserContext.getUser();
        User user = getById(userId);

        String correctCode = stringRedisTemplate.opsForValue().get(RedisConstant.VERIFY_CODE_PREFIX + verifyIdentityDTO.getEmail());
        if (!verifyIdentityDTO.getVerifyCode().equals(correctCode)) {
            return Result.error("验证码错误");
        }

        user.setVerified(true);
        user.setUpdateTime(LocalDateTime.now());
        user.setStudentId(verifyIdentityDTO.getEmail().split("@")[0]);
        if (!updateById(user)) {
            return Result.error("系统错误，请稍后再试");
        }

        stringRedisTemplate.delete(RedisConstant.VERIFY_CODE_PREFIX + verifyIdentityDTO.getEmail());

        final String key = cn.org.joinup.user.constant.RedisConstant.USER_INFO_KEY_PREFIX + user.getId();
        stringRedisTemplate.delete(key);

        return Result.success();
    }

    @Override
    public Result<Void> updateUserInfo(UpdateUserDTO updateUserDTO) {
        if (sensitiveWordBs.contains(updateUserDTO.getUsername())) {
            return Result.error("用户名包含敏感词");
        }

        User user = BeanUtil.copyProperties(updateUserDTO, User.class);
        user.setId(UserContext.getUser());
        user.setUpdateTime(LocalDateTime.now());
        user.setSsoPassword(null);
        Optional.ofNullable(updateUserDTO.getSsoPassword())
                .filter(StrUtil::isNotBlank)
                .ifPresent(pwd -> user.setSsoPassword(PasswordUtil.encrypt(pwd)));
        if (!updateById(user)) {
            return Result.error("更新用户信息失败，请稍后再试");
        }

        final String key = cn.org.joinup.user.constant.RedisConstant.USER_INFO_KEY_PREFIX + user.getId();
        stringRedisTemplate.delete(key);

        return Result.success();
    }

    @Override
    public Result<UserLoginVO> refreshToken() {
        Long userId = UserContext.getUser();
        User user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        String token = jwtTool.createToken(user.getId(), user.getRole(), jwtProperties.getTokenTTL());
        UserLoginVO userLoginVO = BeanUtil.copyProperties(user, UserLoginVO.class);
        userLoginVO.setToken(token);
        return Result.success(userLoginVO);
    }

    @Override
    public String getSsoPassword() {
        Long userId = UserContext.getUser();
        User user = getUserById(userId);
        if (user == null) {
            return null;
        }
        String ssoPassword = user.getSsoPassword();
        if (StrUtil.isBlank(ssoPassword)) {
            return null;
        }
        return ssoPassword;
    }

    @Override
    public User getUserById(Long id) {
        final String key = cn.org.joinup.user.constant.RedisConstant.USER_INFO_KEY_PREFIX + id;
        if (stringRedisTemplate.hasKey(key)) {
            String userJson = stringRedisTemplate.opsForValue().get(key);
            if (StrUtil.isBlank(userJson)) {
                return null;
            } else {
                return JSONUtil.toBean(userJson, User.class);
            }
        }
        User user = getById(id);
        if (user != null) {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(user), cn.org.joinup.user.constant.RedisConstant.USER_INFO_TTL, TimeUnit.SECONDS);
        }
        return user;
    }

    @Override
    public User registerThirdPartyUser(RegisterThirdPartyUserDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setAppKey(registerDTO.getAppKey());
        user.setAppUUID(registerDTO.getAppUUID());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
        return user;
    }
}
