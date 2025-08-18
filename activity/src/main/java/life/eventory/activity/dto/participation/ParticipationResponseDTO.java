package life.eventory.activity.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ParticipationResponse DTO", description = "행사 참여 응답 DTO")
public class ParticipationResponseDTO {
    @Schema(description = "사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "참여한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "행사 참여 시각", example = "2025-08-15T14:30:00")
    private LocalDateTime joinedAt;
}
