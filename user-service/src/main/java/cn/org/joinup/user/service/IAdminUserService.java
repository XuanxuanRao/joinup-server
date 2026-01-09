package cn.org.joinup.user.service;

import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.user.domain.po.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminUserService extends IService<User> {

    IPage<User> getPageUsers(Pageable pageable);

    IPage<User> getPageUsersSearchUsername(String name, Pageable pageable);

    IPage<User> getPageUsersSearchStudentId(String studentId, Pageable pageable);

    List<UserDTO> onlineUsers();

    IPage<User> onlineUsersList(Pageable pageable);

    boolean removeOnlineUser(Long userId, String connectType);
}
