package life.eventory.activity.dto.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BookmarkResponseDTO", description = "북마크 응답 DTO")

public class BookmarkResponseDTO {
    @Schema(description = "북마크를 생성한 사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "북마크한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "북마크 한 시간", example = "2025-08-15T14:30:00")
    private LocalDateTime createdAt;

}
