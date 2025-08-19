package life.eventory.ai.dto.activity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationResponse {
    private Long userId;
    private Long eventId;
    private LocalDateTime joinedAt;
}
