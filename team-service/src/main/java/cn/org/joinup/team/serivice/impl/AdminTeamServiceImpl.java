package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.mapper.TeamMapper;
import cn.org.joinup.team.serivice.IAdminTeamService;
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
public class AdminTeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements IAdminTeamService {
    
    @Override
    public IPage<Team> getPageTeams(Pageable pageable) {
        Page<Team> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        return this.page(page);
    }

    @Override
    public IPage<Team> getPageTeamsSearch(String name, Pageable pageable) {
        Page<Team> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return this.baseMapper.selectPage(page, wrapper);
    }

}
