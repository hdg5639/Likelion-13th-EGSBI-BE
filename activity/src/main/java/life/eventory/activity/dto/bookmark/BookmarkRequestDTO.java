package life.eventory.activity.dto.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BookmarkRequestDTO", description = "북마크 요청 DTO")
public class BookmarkRequestDTO {
    @Schema(description = "북마크한 이벤트 ID", example = "1000")
    private Long eventId;
}
