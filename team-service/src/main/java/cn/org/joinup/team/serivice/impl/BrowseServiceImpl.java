package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.date.DateTime;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.common.util.UserContext;
import cn.org.joinup.team.domain.po.BrowseHistory;
import cn.org.joinup.team.domain.vo.TeamBrowseVO;
import cn.org.joinup.team.mapper.BrowseHistoryMapper;
import cn.org.joinup.team.serivice.IBrowseService;
import cn.org.joinup.team.serivice.ITeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service  // 标记为service的服务层
@RequiredArgsConstructor //Lombok注解，为final字段生成构造方法，用于依赖注入
public class BrowseServiceImpl extends ServiceImpl<BrowseHistoryMapper,BrowseHistory> implements IBrowseService {
    // ServiceImpl：第二个为实体类，第一个是实体类对应的mapper接口，用于提供CURD方法：
    /*
    * boolean save(BrowseHistory entity);          // 插入
    * boolean removeById(Serializable id);         // 删除
    * boolean updateById(BrowseHistory entity);    // 更新
    * BrowseHistory getById(Serializable id);      // 查询
    * List<BrowseHistory> list();                  // 查询所有
    * */
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
        List<BrowseHistory> histories = lambdaQuery()
                .eq(BrowseHistory::getUserId, UserContext.getUser())
                .orderByDesc(BrowseHistory::getCreateTime).list();

        HashSet<Long> teamIds = new HashSet<>();
        ArrayList<BrowseHistory> finalHistories = new ArrayList<>();
        for(BrowseHistory history:histories){
            if(!teamIds.contains(history.getTeamId())){
                teamIds.add(history.getTeamId());
                finalHistories.add(history);
            }
        }
        return Result.success(finalHistories.stream().map(this::convertToTeamBrowseVO).collect(Collectors.toList()));
    }

    private TeamBrowseVO convertToTeamBrowseVO(BrowseHistory browseHistory){
        return TeamBrowseVO.builder()
                .id(browseHistory.getId())
                .createTime(browseHistory.getCreateTime())
                .team(teamService.convertToBriefTeamVO(teamService.getById(browseHistory.getTeamId())))
                .build();
    }
}
