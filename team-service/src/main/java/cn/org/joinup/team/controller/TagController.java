package cn.org.joinup.team.controller;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.dto.AddTagDTO;
import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.serivice.ITagApplicationService;
import cn.org.joinup.team.serivice.ITagService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final ITagApplicationService tagApplicationService;
    private final ITagService tagService;

    @PostMapping("/apply")
    @ApiOperation("用户申请创建标签")
    public Result<Void> applyTag(@RequestBody AddTagDTO addTagDTO) {
        return tagApplicationService.addTagApplication(addTagDTO);
    }

    @GetMapping("/list")
    @ApiOperation("获取所有标签")
    public Result<List<Tag>> list() {
        return Result.success(tagService.list());
    }

}
