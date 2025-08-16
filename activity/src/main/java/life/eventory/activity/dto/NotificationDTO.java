package life.eventory.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "NotificationDTO", description = "사용자가 특정 이벤트를 알림 설정했는지 정보를 담는 DTO")

public class NotificationDTO {
    @Schema(description = "알림 ID (자동 생성)", example = "1")
    private Long id;

    @Schema(description = "알림을 설정한 사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "알림한 설정한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "알림 등록 시각", example = "2025-08-15T14:30:00")
    private LocalDateTime createdAt;
}
