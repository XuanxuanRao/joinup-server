package cn.org.joinup.team.controller.admin;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.serivice.IAdminTeamMemberService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/team-member")
@RequiredArgsConstructor
@Api(tags = "管理员成员接口")
public class AdminTeamMemberController {
    private final IAdminTeamMemberService iAdminTeamMemberService;

    @GetMapping("/count")
    public Result<Long> count() {
        Long count = iAdminTeamMemberService.count();
        return Result.success(count);
    }

    // 分页加载数据
    @GetMapping("/list")
    public IPage<TeamMember> list(Pageable pageable) {
        return iAdminTeamMemberService.getPageTeamMembers(pageable);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        iAdminTeamMemberService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result<Void> deleteBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.get("ids");
        iAdminTeamMemberService.removeByIds(ids);
        return Result.success();
    }

}
