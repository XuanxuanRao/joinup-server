package cn.org.joinup.team.serivice;

import cn.org.joinup.common.result.Result;
import cn.org.joinup.team.domain.po.Theme;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

public interface IAdminThemeService extends IService<Theme> {

    public IPage<Theme> getPageThemes(Pageable pageable);

    public IPage<Theme> getPageThemesSearch(String name, Pageable pageable);
}
