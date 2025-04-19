package cn.org.joinup.user.service;

import cn.org.joinup.user.domain.po.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminUserService extends IService<User> {

    public IPage<User> getPageUsers(Pageable pageable);

    public IPage<User> getPageUsersSearchUsername(String name, Pageable pageable);

    public IPage<User> getPageUsersSearchStudentId(String studentId, Pageable pageable);
}
