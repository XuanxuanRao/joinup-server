package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.Team;
import cn.org.joinup.team.mapper.TeamMapper;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements ITeamService {

}
