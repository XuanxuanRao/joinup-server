package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.BrowseHistory;
import cn.org.joinup.team.serivice.IBrowseService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController   //标记当前类是一个REST风格的控制器，所有方法的返回值默认会被转换为JSON/XML响应体
@RequestMapping("/team")
@RequiredArgsConstructor
@Api(tags = "队伍接口")
public class BrowseHistoryController {
    private final IBrowseService browseHistoryService;

    @PostMapping("/{teamId}/browse")
    public Result<BrowseHistory> browseTeam(@PathVariable Long teamId){
        return browseHistoryService.addNewHistory(teamId);
    }

    @GetMapping("/browse")
    public Result<List<BrowseHistory>> getBrowseHistory(){
        return browseHistoryService.getUserBrowseHistory();
    }
}
