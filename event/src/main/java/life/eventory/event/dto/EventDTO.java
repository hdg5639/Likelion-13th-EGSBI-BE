package life.eventory.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
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
    private Integer entryFee;
}
