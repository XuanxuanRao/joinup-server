package cn.org.joinup.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author chenxuanrao06@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCodeVO {
    private String scanId;
    private String link;
}
