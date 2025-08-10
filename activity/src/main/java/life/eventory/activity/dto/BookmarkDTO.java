package life.eventory.activity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private Long bookmarkId;
    private Long userId;
    private Long eventId;
}
