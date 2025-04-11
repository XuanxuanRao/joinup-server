package cn.org.joinup.team.serivice;

import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.domain.vo.TeamMemberVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
public interface ITeamMemberService extends IService<TeamMember> {
    boolean isTeamMember(Long teamId, Long userId);

    List<TeamMemberVO> getTeamMembersByTeamId(Long teamId);
}
