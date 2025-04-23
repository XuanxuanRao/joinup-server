package cn.org.joinup.team.serivice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.org.joinup.api.client.UserClient;
import cn.org.joinup.api.dto.UserDTO;
import cn.org.joinup.team.constants.RedisConstant;
import cn.org.joinup.team.domain.po.TeamMember;
import cn.org.joinup.team.domain.vo.TeamMemberVO;
import cn.org.joinup.team.enums.TeamMemberRole;
import cn.org.joinup.team.mapper.TeamMemberMapper;
import cn.org.joinup.team.serivice.ITeamMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chenxuanrao06@gmail.com
 */
@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements ITeamMemberService {

    private final UserClient userClient;
    private final StringRedisTemplate stringRedisTemplate;
    
    @Override
    public boolean isTeamMember(Long teamId, Long userId) {
        return getJoinedTeamIds(userId).contains(teamId) || getCreatedTeamIds(userId).contains(teamId);
    }

    @Override
    public List<TeamMemberVO> getTeamMembersByTeamId(Long teamId) {
        List<TeamMember> members = lambdaQuery()
                .eq(TeamMember::getTeamId, teamId)
                .list();

        return members.stream()
                .map(member -> {
                    TeamMemberVO teamMemberVO = BeanUtil.copyProperties(member, TeamMemberVO.class);
                    UserDTO userInfo = userClient.queryUser(member.getUserId()).getData();
                    teamMemberVO.setUserName(userInfo.getUsername());
                    teamMemberVO.setAvatar(userInfo.getAvatar());
                    return teamMemberVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> getJoinedTeamIds(Long userId) {
        String key = RedisConstant.JOIN_TEAM_KEY_PREFIX + userId;

        // 1. 先查缓存，如果缓存命中，直接返回
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return Objects.requireNonNull(stringRedisTemplate.opsForSet().members(key)).stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }

        // 2. 缓存未命中，查询数据库
        Set<Long> teamIds = lambdaQuery()
                .eq(TeamMember::getUserId, userId)
                .eq(TeamMember::getRole, TeamMemberRole.MEMBER)
                .select(TeamMember::getTeamId)
                .list()
                .stream()
                .map(TeamMember::getTeamId)
                .collect(Collectors.toSet());

        // 3. 将查询结果存入缓存
        String[] redisValue = teamIds.stream().map(String::valueOf).distinct().toArray(String[]::new);
        if (redisValue.length > 0) {
            stringRedisTemplate.opsForSet().add(key, redisValue);
            stringRedisTemplate.expire(key, RedisConstant.JOIN_TEAM_CACHE_TTL, TimeUnit.SECONDS);
        }

        // 4. 返回结果
        return teamIds;
    }

    @Override
    public Set<Long> getCreatedTeamIds(Long userId) {
        String key = RedisConstant.CREATE_TEAM_KEY_PREFIX + userId;

        // 1. 先查缓存，如果缓存命中，直接返回
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return Objects.requireNonNull(stringRedisTemplate.opsForSet().members(key)).stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }

        // 2. 缓存未命中，查询数据库
        Set<Long> teamIds = lambdaQuery()
                .eq(TeamMember::getUserId, userId)
                .eq(TeamMember::getRole, TeamMemberRole.CREATOR)
                .select(TeamMember::getTeamId)
                .list()
                .stream()
                .map(TeamMember::getTeamId)
                .collect(Collectors.toSet());

        // 3. 将查询结果存入缓存
        String[] redisValue = teamIds.stream().map(String::valueOf).distinct().toArray(String[]::new);
        if (redisValue.length > 0) {
            stringRedisTemplate.opsForSet().add(key, redisValue);
            stringRedisTemplate.expire(key, RedisConstant.CREATE_TEAM_CACHE_TTL, TimeUnit.SECONDS);
        }

        // 4. 返回结果
        return teamIds;
    }

    @Override
    public TeamMemberRole getUserRole(Long teamId, Long userId) {
        if (getCreatedTeamIds(userId).contains(teamId)) {
            return TeamMemberRole.CREATOR;
        } else if (getJoinedTeamIds(userId).contains(teamId)) {
            return TeamMemberRole.MEMBER;
        } else {
            return null;
        }
    }
}

