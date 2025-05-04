package cn.org.joinup.team.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
@ApiModel("审批加入队伍申请表单")
public class JoinReviewAction {
    @ApiModelProperty("审批操作，0：同意，1：拒绝")
    @Range(min = 0, max = 1, message = "Invalid Parameter: joinReviewAction.action")
    private Integer action;
    @ApiModelProperty("审批意见")
    private String comment;
}
