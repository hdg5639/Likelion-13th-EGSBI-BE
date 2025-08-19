package life.eventory.ai.dto.activity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private Long userId;
    private Long eventId;
    private LocalDateTime createdAt;
    private Long bookmarkCount;
}
