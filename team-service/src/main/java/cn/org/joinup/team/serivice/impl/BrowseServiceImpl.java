package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.date.DateTime;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.po.BrowseHistory;
import cn.org.joinup.team.domain.vo.TeamBrowseVO;
import cn.org.joinup.team.mapper.BrowseHistoryMapper;
import cn.org.joinup.team.serivice.IBrowseService;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BrowseServiceImpl extends ServiceImpl<BrowseHistoryMapper,BrowseHistory> implements IBrowseService {

    private final ITeamService teamService;

    @Override
    public Result<BrowseHistory> addNewHistory(Long teamId) {

       BrowseHistory browseHistory = new BrowseHistory();
       browseHistory.setTeamId(teamId);
       browseHistory.setUserId(UserContext.getUser());
       browseHistory.setCreateTime(DateTime.now().toLocalDateTime());

       if(!save(browseHistory)) {
           return Result.error("写入浏览记录失败");
       }
       return Result.success(browseHistory);
    }

    @Override
    public Result<List<TeamBrowseVO>> getUserBrowseHistory() {
        List<Map<String, Object>> histories = baseMapper.selectMaps(new QueryWrapper<BrowseHistory>()
                .select("team_id", "max(create_time) as last_browse_time")
                .eq("user_id", UserContext.getUser())
                .groupBy("team_id", "user_id")
                .orderByDesc("last_browse_time"));

        return Result.success(histories.stream()
                .map(his -> {
                    TeamBrowseVO teamBrowseVO = new TeamBrowseVO();
                    teamBrowseVO.setCreateTime(((java.sql.Timestamp) his.get("last_browse_time")).toLocalDateTime());
                    teamBrowseVO.setTeam(teamService.convertToBriefTeamVO(teamService.getById((Long) his.get("team_id"))));
                    return teamBrowseVO;
                })
                .collect(Collectors.toList()));
    }

}
