package cn.org.joinup.team.mapper;

import cn.org.joinup.team.domain.po.BrowseHistory; // 实体类，对应数据库表
import com.baomidou.mybatisplus.core.mapper.BaseMapper;  // BaseMapper基类，提供预定义CURD方法
import org.apache.ibatis.annotations.Mapper;

@Mapper  // 标识该接口由MyBatis管理
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory>{
}
