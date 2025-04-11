package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.domain.vo.TeamMemberVO;
import cn.org.joinup.team.mapper.TeamMemberMapper;
import cn.org.joinup.team.serivice.ITeamMemberService;
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
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements ITeamMemberService {

    private final UserClient userClient;
    
    @Override
    public boolean isTeamMember(Long teamId, Long userId) {
        return lambdaQuery()
                .eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, userId)
                .exists();
    }

    @Override
    public List<TeamMemberVO> getTeamMembersByTeamId(Long teamId) {
        List<TeamMember> members = lambdaQuery()
                .eq(TeamMember::getTeamId, teamId)
                .list();

        return members.stream()
                .map(member -> {
                    TeamMemberVO teamMemberVO = BeanUtil.copyProperties(member, TeamMemberVO.class);
                    UserDTO userInfo = userClient.queryUser(member.getUserId()).getData();
                    teamMemberVO.setUserName(userInfo.getUsername());
                    teamMemberVO.setAvatar(userInfo.getAvatar());
                    return teamMemberVO;
                })
                .collect(Collectors.toList());
    }
}

