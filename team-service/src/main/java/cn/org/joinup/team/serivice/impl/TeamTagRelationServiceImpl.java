package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.TeamTagRelation;
import cn.org.joinup.team.mapper.TeamTagRelationMapper;
import cn.org.joinup.team.serivice.ITagService;
import cn.org.joinup.team.serivice.ITeamTagRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TeamTagRelationServiceImpl extends ServiceImpl<TeamTagRelationMapper, TeamTagRelation> implements ITeamTagRelationService {

    private final ITagService tagService;

    @Override
    public List<Tag> getTagsByTeamId(Long teamId) {
        return lambdaQuery()
                .eq(TeamTagRelation::getTeamId, teamId)
                .list()
                .stream()
                .map(TeamTagRelation::getTagId)
                .map(tagService::getById)
                .collect(Collectors.toList());
    }
}
