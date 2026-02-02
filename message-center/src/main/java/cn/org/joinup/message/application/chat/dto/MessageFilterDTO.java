package cn.org.joinup.message.application.chat.dto;


import cn.org.joinup.common.enums.ChatMessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ApiModel("对会话中的消息进行过滤")
@Data
public class MessageFilterDTO {
    @ApiModelProperty("消息发送成员")
    private Long senderId;

    @ApiModelProperty("消息类型")
    private ChatMessageType messageType;

    @ApiModelProperty("消息日期")
    private LocalDate messageDate;

    @ApiModelProperty("消息内容")
    private String messageContent;

    @ApiModelProperty("查询页码")
    @NotNull
    private Integer pageNumber;

    @ApiModelProperty("页大小")
    @NotNull
    private Integer pageSize;
}
