package life.eventory.activity.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {
    private Long historyId;
    private Long userId;
    private Long eventId;

    private String eventName;
    private String eventThumbnail;

    private LocalDateTime viewedAt; // 마지막 조회 시간
}
