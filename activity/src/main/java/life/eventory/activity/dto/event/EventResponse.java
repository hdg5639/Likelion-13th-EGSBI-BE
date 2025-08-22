package life.eventory.activity.dto.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private Long organizerId;
    private String name;
    private Long posterId;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long entryFee;
    private LocalDateTime createTime;
    private Long qrImage;
    private List<String> hashtags;
}
