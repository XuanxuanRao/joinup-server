package cn.org.joinup.message.controller;

import cn.org.joinup.common.result.PageResult;
import cn.org.joinup.common.result.Result;
import cn.org.joinup.message.domain.po.SiteMessage;
import cn.org.joinup.message.enums.NotifyType;
import cn.org.joinup.message.service.ISiteMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * @author chenxuanrao06@gmail.com
 */
@RestController
@RequestMapping("/message/site")
@RequiredArgsConstructor
@Api(tags = "站内信接口")
public class SiteMessageController {

    private final ISiteMessageService siteMessageService;

    @GetMapping("/list")
    @ApiOperation("获取站内信列表")
    public Result<PageResult<SiteMessage>> list(
            @RequestParam(required = false) Boolean read,
            @RequestParam(required = false) NotifyType type,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "1") Integer pageNumber) {
        return Result.success(PageResult.of(siteMessageService.pageQuery(read, type, pageSize, pageNumber)));
    }

    @PostMapping("/read/{id}")
    @ApiOperation("标记站内信已读")
    public Result<Void> read(@PathVariable Long id) {
        return siteMessageService.setReadStatus(id, true);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除站内信")
    public Result<Void> delete(@PathVariable Long id) {
        return siteMessageService.deleteMessage(id);
    }

}
