package cn.org.joinup.course.service.impl;

import cn.org.joinup.course.mapper.SignLogMapper;
import cn.org.joinup.course.service.ISignLogService;

import cn.org.joinup.course.domain.po.SignLog;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class SignLogServiceImpl extends ServiceImpl<SignLogMapper, SignLog> implements ISignLogService {

}
