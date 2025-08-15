package life.eventory.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BookmarkDTO", description = "사용자가 특정 이벤트를 북마크했는지 정보를 담는 DTO")
public class BookmarkDTO {

    @Schema(description = "북마크 ID (자동 생성)", example = "1")
    private Long id;

    @Schema(description = "북마크를 생성한 사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "북마크한 이벤트 ID", example = "1000")
    private Long eventId;
}
