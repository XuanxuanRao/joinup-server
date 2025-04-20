package cn.org.joinup.team.serivice;

import cn.org.joinup.api.dto.UserTeamStatisticDTO;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.CreateTeamDTO;
import cn.org.joinup.team.domain.dto.UpdateTeamInfoDTO;
import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.domain.vo.TeamVO;
import cn.org.joinup.team.enums.TeamMemberRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ITeamService extends IService<Team> {
    Result<TeamVO> userGetTeam(Long teamId);

    Result<Void> updateTeamInfo(Long teamId, UpdateTeamInfoDTO updateTeamInfoDTO);

    Result<Team> createTeam(CreateTeamDTO createTeamDTO);

    boolean addMember(Long teamId, Long userId);

    Result<List<Team>> getParticipatedTeam(TeamMemberRole role);

    Result<UserTeamStatisticDTO> getMyTeamCount();

    Result<Void> leaveTeam(Long teamId);

}
