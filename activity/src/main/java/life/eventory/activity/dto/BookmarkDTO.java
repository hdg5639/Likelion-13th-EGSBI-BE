package life.eventory.activity.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private Long id;
    private Long userId;
    private Long eventId;
}
