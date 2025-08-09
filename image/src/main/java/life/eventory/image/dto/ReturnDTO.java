package life.eventory.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이미지 ID 반환")
public class ReturnDTO {
    @Schema(description = "이미지 ID", example = "1")
    private Long imageId;
}
