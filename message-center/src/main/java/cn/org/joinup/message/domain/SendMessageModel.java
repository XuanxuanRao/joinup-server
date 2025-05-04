package cn.org.joinup.message.domain;

import cn.org.joinup.message.enums.MessageType;
import cn.org.joinup.message.enums.PushChannel;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 消息发送抽象模型
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("消息发送抽象模型")
public class SendMessageModel {
    @ApiModelProperty("消息类型，0表示验证码，1表示通知")
    @NotNull
    private MessageType messageType;

    @ApiModelProperty("推送方式，0表示邮箱，1表示站内信，2表示微信")
    @NotNull
    private PushChannel channel;

    @ApiModelProperty("模板编码")
    @NotNull
    private String templateCode;

    @ApiModelProperty("模板参数")
    private Map<String, Object> params;
}
