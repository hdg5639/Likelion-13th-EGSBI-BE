package life.eventory.activity.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HistoryRequestDTO", description = "사용자 활동 히스토리 정보 요청 DTO")
public class HistoryRequestDTO {
    @Schema(description = "행사 ID", example = "2001")
    private Long eventId;

    @Schema(description = "행사 이름", example = "뮤직 페스티벌 2025")
    private String eventName;

    @Schema(description = "행사 썸네일 URL", example = "https://example.com/image.jpg")
    private String eventThumbnail;

}
