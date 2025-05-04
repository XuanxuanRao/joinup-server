package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.mapper.TeamMemberMapper;
import cn.org.joinup.team.serivice.IAdminTeamMemberService;
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
public class AdminTeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements IAdminTeamMemberService {

    @Override
    public IPage<TeamMember> getPageTeamMembers(Pageable pageable) {
        Page<TeamMember> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<TeamMember> getPageTeamMembersSearch(String name, Pageable pageable) {
        Page<TeamMember> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<TeamMember> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return this.baseMapper.selectPage(page, wrapper);
    }
}
