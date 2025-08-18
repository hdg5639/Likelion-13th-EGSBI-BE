package life.eventory.activity.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "NotificationRequestDTO", description = "알림 설정 요청 DTO")

public class NotificationRequestDTO {

    @Schema(description = "알림한 설정한 이벤트 ID", example = "1000")
    private Long eventId;
}
