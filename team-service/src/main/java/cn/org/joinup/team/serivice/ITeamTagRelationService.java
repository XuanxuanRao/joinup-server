package cn.org.joinup.team.serivice;

import cn.org.joinup.team.domain.po.Tag;
import cn.org.joinup.team.domain.po.TeamTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chenxuanrao06@gmail.com
 */
public interface ITeamTagRelationService extends IService<TeamTagRelation> {
    List<Tag> getTagsByTeamId(Long teamId);

    List<Long> getTeamIdsByTagId(Integer tagId);

}
