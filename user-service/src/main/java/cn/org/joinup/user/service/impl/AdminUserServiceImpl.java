package cn.org.joinup.user.service.impl;

import cn.org.joinup.user.domain.po.User;
import cn.org.joinup.user.mapper.UserMapper;
import cn.org.joinup.user.service.IAdminUserService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl extends ServiceImpl<UserMapper, User> implements IAdminUserService {

    // 分页获取用户
    @Override
    public IPage<User> getPageUsers(Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<User> getPageUsersSearchUsername(String name, Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "username", name);

        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<User> getPageUsersSearchStudentId(String studentId, Pageable pageable) {
        Page<User> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(studentId), "student_id", studentId);

        return this.baseMapper.selectPage(page, wrapper);
    }
}
