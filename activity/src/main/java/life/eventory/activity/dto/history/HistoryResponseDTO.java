package life.eventory.activity.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HistoryResponseDTO", description = "사용자 활동 히스토리 정보 응답 DTO")
public class HistoryResponseDTO {

    @Schema(description = "사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "행사 ID", example = "2001")
    private Long eventId;

    @Schema(description = "행사 이름", example = "뮤직 페스티벌 2025")
    private String eventName;

    @Schema(description = "행사 썸네일 URL", example = "https://example.com/image.jpg")
    private String eventThumbnail;

    @Schema(description = "마지막 조회 시간", example = "2025-08-15T14:30:00")
    private LocalDateTime viewedAt; // 마지막 조회 시간
}
