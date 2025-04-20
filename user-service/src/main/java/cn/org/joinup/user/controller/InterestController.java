package cn.org.joinup.user.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.user.domain.po.Interest;
import cn.org.joinup.user.service.IInterestService;
import cn.org.joinup.user.service.IUserInterestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/interest")
@RequiredArgsConstructor
@Api(tags = "用户兴趣/技术标签接口")
public class InterestController {
    private final IInterestService interestService;
    private final IUserInterestService userInterestService;

    @GetMapping("/all")
    public Result<List<Interest>> listAll() {
        return Result.success(interestService.getInterests());
    }

    @ApiOperation("获取用户的兴趣")
    @GetMapping("/my")
    public Result<List<Interest>> getUserInterests() {
        return Result.success(userInterestService.getUserInterests(null));
    }

    @ApiOperation("添加用户兴趣")
    @PostMapping("/add")
    public Result<Void> addUserInterest(@RequestParam Long interestId) {
        userInterestService.addUserInterest(interestId);
        return Result.success();
    }

    @ApiOperation("删除用户兴趣")
    @DeleteMapping("/{interestId}")
    public Result<Void> deleteUserInterest(@PathVariable Long interestId) {
        userInterestService.deleteUserInterest(interestId);
        return Result.success();
    }


}
