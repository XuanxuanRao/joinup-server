package cn.org.joinup.team.domain.dto;

import lombok.Data;

/**
 * @author chenxuanrao06@gmail.com
 */
@Data
public class TagReviewAction {
    // 0: 通过，1: 拒绝

    private Integer action;

    private String comment;
}
