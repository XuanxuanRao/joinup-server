package cn.org.joinup.user.service;

import cn.org.joinup.common.exception.SystemException;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.dto.LoginFormDTO;
import cn.org.joinup.user.domain.dto.RegisterFormDTO;
import cn.org.joinup.user.domain.dto.ResetPasswordDTO;
import cn.org.joinup.user.domain.dto.VerifyIdentityDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.UserLoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginDTO);
    UserLoginVO wxLogin(String code) throws SystemException;

    UserLoginVO register(RegisterFormDTO registerDTO);

    Result<Void> resetPassword(ResetPasswordDTO resetPasswordDTO);

    Result<Void> verifyIdentity(VerifyIdentityDTO verifyIdentityDTO);
}
