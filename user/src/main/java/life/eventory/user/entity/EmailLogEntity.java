package life.eventory.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_email_log")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "이메일 로그 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "주최자 ID", example = "1")
    private Long organizerId;

    @Schema(description = "행사 ID", example = "11")
    private Long eventId;

    @Schema(description = "이메일 주소", example = "seojin57913@naver.com")
    private String email;

    @Schema(description = "이메일 전송 시간", example = "2025-08-20T15:00:00")
    private LocalDateTime sendTime;
}
