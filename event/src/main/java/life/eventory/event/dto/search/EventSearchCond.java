package life.eventory.event.dto.search;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventSearchCond {
    private String q;
    private List<String> tags;
    private Long organizerId;

    private LocalDateTime startFrom;
    private LocalDateTime startTo;

    private Boolean freeOnly;
    private Integer minFee;
    private Integer maxFee;

    private Double lat;
    private Double lon;
    private Double radiusKm;

    private EventSort sort = EventSort.LATEST;
}
