package life.eventory.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이미지 업로드 후 반환되는 정보")
public class ResponseDTO {
    @Schema(description = "이미지 고유 ID", example = "123")
    private Long id;
    @Schema(description = "저장된 파일 이름 (UUID 기반)", example = "f1f2445a-9e41-4a92-ae6c-bb74d5b5a5c9.jpg")
    private String storedFileName;
}
