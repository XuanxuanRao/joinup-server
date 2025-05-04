package cn.org.joinup.team.serivice;

import cn.org.joinup.team.domain.po.TeamMember;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminTeamMemberService extends IService<TeamMember> {

    IPage<TeamMember> getPageTeamMembers(Pageable pageable);

    IPage<TeamMember> getPageTeamMembersSearch(String name, Pageable pageable);

}
