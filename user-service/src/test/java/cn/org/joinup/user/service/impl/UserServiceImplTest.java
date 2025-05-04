package cn.org.joinup.user.service.impl;

import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
public class UserServiceImplTest {

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected IUserService userService;

    @Test
    public void setPassword() {
        User user = userService.getById(2);
        user.setPassword(passwordEncoder.encode("rcx123456"));
        userService.updateById(user);
    }

}