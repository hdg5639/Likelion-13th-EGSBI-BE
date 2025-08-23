package life.eventory.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "AI 행사 추천 프롬프트")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class AiComment {
    @Schema(description = "프롬프트 원문")
    private String prompt;
}
