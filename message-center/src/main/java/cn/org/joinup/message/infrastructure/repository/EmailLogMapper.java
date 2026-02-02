package cn.org.joinup.message.infrastructure.repository;

import cn.org.joinup.message.domain.message.entity.EmailLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenxuanrao06@gmail.com
 */
@Mapper
public interface EmailLogMapper extends BaseMapper<EmailLog> {
}
