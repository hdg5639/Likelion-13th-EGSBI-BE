package life.eventory.activity.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReviewRequest DTO", description = "리뷰 요청 DTO")
public class ReviewRequestDTO {

    @Schema(description = "참여한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "리뷰 내용", example = "굿이에요")
    private String content;

    @Schema(description = "행사 별점", example = "4")
    private Integer rating;
}
