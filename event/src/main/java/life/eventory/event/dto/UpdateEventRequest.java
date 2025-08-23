package life.eventory.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "이벤트 수정 요청 객체")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    @Schema(description = "이벤트 본문(JSON)")
    private EventUpdate event;

    @Schema(type = "string", format = "binary", description = "포스터 이미지 파일")
    private MultipartFile image;
}