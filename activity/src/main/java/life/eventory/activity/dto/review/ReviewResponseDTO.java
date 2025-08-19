package life.eventory.activity.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReviewResponse DTO", description = "리뷰 응답 DTO")
public class ReviewResponseDTO {
    @Schema(description = "사용자 ID", example = "100")
    private Long userId;

    @Schema(description = "참여한 이벤트 ID", example = "1000")
    private Long eventId;

    @Schema(description = "리뷰 내용", example = "굿이에요")
    private String content;

    @Schema(description = "행사 별점", example = "4")
    private Integer rating;

    @Schema(description = "리뷰 작성 시각", example = "2025-08-15T14:30:00")
    private LocalDateTime createdAt;

}
