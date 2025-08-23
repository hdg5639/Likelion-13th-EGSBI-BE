package life.eventory.activity.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ParticipationRequest DTO", description = "행사 참여 요청 DTO")
public class ParticipationRequestDTO {
    @Schema(description = "참여한 이벤트 ID", example = "1000")
    private Long eventId;
}
