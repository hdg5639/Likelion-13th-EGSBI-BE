package life.eventory.event.dto.activity;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRequest {
    private Long eventId;
    private String name;
    private Long posterId;
}
