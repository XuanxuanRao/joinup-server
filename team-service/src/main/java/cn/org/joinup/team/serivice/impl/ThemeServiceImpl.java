package cn.org.joinup.team.serivice.impl;

import cn.org.joinup.team.domain.po.Theme;
import cn.org.joinup.team.mapper.ThemeMapper;
import cn.org.joinup.team.serivice.IThemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
public class ThemeServiceImpl extends ServiceImpl<ThemeMapper, Theme> implements IThemeService {
}