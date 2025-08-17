package life.eventory.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserEvent DTO", description = "사용자 행사 리스트 조회 DTO")
public class UserEventDTO {
    @Schema(description = "자동생성 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "101")
    private Long userId;

    @Schema(description = "행사 ID", example = "101")
    private Long eventId;

    @Schema(description = "행사 이름", example = "여름 캠프")
    private String eventName;

    @Schema(description = "행사 날짜", example = "2025-08-17")
    private String date;
}
