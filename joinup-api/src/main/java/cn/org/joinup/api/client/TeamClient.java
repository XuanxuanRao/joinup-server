package cn.org.joinup.api.client;

import cn.org.joinup.api.dto.UserTeamStatisticDTO;
import cn.org.joinup.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "team-service")
public interface TeamClient {
    @GetMapping("/team/my/count")
    Result<UserTeamStatisticDTO> getMyTeamCount();

}
