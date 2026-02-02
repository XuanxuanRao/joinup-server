package cn.org.joinup.message.interfaces.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureVO {
    private String featureName;
    private Boolean isPublic;
    private Integer whiteListCount;
}
