package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "구독 생성 요청 DTO")
public class SubscriptionCreateRequest {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "주최자 ID", example = "0")
    private Long organizerId;
}
