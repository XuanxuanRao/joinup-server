package cn.org.joinup.user.service;

import cn.org.joinup.common.exception.SystemException;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.*;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginDTO);
    UserLoginVO wxLogin(String code) throws SystemException;

    UserLoginVO register(RegisterFormDTO registerDTO);

    Result<UserLoginVO> wxRegister(WxRegisterFormDTO wxRegisterFormDTO);

    Result<Void> resetPassword(ResetPasswordDTO resetPasswordDTO);

    Result<Void> verifyIdentity(VerifyIdentityDTO verifyIdentityDTO);

    Result<Void> updateUserInfo(UpdateUserDTO updateUserDTO);

    Result<UserLoginVO> refreshToken();

    String getSsoPassword();

    User getUserById(Long id);

    User registerThirdPartyUser(RegisterThirdPartyUserDTO registerDTO) throws SystemException;
}
