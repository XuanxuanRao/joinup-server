package cn.org.joinup.message.service.impl;

import cn.org.joinup.message.domain.po.ChatMessage;
import cn.org.joinup.message.mapper.ChatMessageMapper;
import cn.org.joinup.message.service.IAdminChatLogService;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminChatLogService extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IAdminChatLogService {
    
    @Override
    public IPage<ChatMessage> getPageChatMessages(Pageable pageable) {
        Page<ChatMessage> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
        // 添加按创建时间倒序排列
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time"); // 按创建时间倒序

        return this.page(page, wrapper);
        // return this.page(page);
    }

    @Override
    public IPage<ChatMessage> getPageChatMessagesSearch(String conversationId, Pageable pageable) {
        Page<ChatMessage> page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());

        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(conversationId), "conversation_id", conversationId);

        wrapper.orderByDesc("create_time"); // 按创建时间倒序

        return this.baseMapper.selectPage(page, wrapper);
    }

    //TODO 根据conversationId(userId, teamId)查询
}
