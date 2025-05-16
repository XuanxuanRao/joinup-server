package cn.org.joinup.team.serivice;


import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.BrowseHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IBrowseService extends IService<BrowseHistory> {

    Result<BrowseHistory> addNewHistory(Long teamId);
    Result<List<BrowseHistory>> getUserBrowseHistory();

}

