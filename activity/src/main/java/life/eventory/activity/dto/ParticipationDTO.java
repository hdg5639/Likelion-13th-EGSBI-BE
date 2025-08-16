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
@Schema(name = "Participation DTO", description = "행사 참여 요청 DTO")

public class ParticipationDTO {
    @Schema(description = "참여 ID (자동 생성)", example = "1")
    private Long id;

    @Schema(description = "참여한 사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "참여한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "행사 참여 시각", example = "2025-08-15T14:30:00")
    private LocalDateTime joinedAt;
}
