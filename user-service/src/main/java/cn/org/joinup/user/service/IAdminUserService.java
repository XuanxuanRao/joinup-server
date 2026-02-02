package cn.org.joinup.user.service;

import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.user.domain.dto.request.QueryUserInfoDTO;
import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.domain.vo.AdminUserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminUserService extends IService<User> {

    PageResult<AdminUserInfoVO> getPageUsers(QueryUserInfoDTO queryUserDTO);

    List<UserDTO> onlineUsers();

    IPage<User> onlineUsersList(Pageable pageable);

    boolean removeOnlineUser(Long userId, String connectType);
}
