package life.eventory.event.dto.recommender;

import io.swagger.v3.oas.annotations.media.Schema;
import life.eventory.event.dto.EventDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AiRecommendResponse {
    @Schema(description = "AI가 생성한 한줄 코멘트", example = "야시장과 음악, 축제를 좋아하는 당신을 위한 추천!")
    private String comment;

    @Schema(description = "추천 행사 목록")
    private List<EventDTO> events;
}


