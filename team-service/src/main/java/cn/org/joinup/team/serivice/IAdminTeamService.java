package cn.org.joinup.team.serivice;

import cn.org.joinup.team.domain.po.Team;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminTeamService extends IService<Team> {

    IPage<Team> getPageTeams(Pageable pageable);

    IPage<Team> getPageTeamsSearch(String name, Pageable pageable);
}
