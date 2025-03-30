package cn.org.joinup.user.service;

import cn.org.joinup.common.result.Result;

/**
 * @author chenxuanrao06@gmail.com
 * @Description:
 */
public interface IVerifyService {

    Result<Void> sendVerifyCodeForRegister(String email);

    Result<Void> sendVerifyCodeForReset(String email);

}
