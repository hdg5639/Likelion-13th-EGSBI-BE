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
    private EventDTO event;

    @Schema(type = "string", format = "binary", description = "포스터 이미지 파일")
    private MultipartFile image;

    @Schema(description = "포스터 존재 유무 플래그 (포스터를 지울 시 false, 이미 있거나, 추가하는거면 true)", example = "true")
    private Boolean poster;
}