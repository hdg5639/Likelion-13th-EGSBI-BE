package life.eventory.ai.dto.activity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private Long userId;
    private Long eventId;
    private String name;
    private Long posterId;
    private LocalDateTime viewedAt;
}