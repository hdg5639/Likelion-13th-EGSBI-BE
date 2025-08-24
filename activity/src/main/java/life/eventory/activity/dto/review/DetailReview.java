package life.eventory.activity.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "리뷰 디테일 객체")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class DetailReview {
    @Schema(example = "1")
    private Long userID;
    @Schema(example = "내용")
    private String content;
    @Schema(example = "점수")
    private Integer rating;
}
