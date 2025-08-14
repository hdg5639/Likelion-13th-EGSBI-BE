package life.eventory.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위치 정보 객체")
public class LocationDTO {
    @Schema(description = "위도(-90~90)", example = "35.8704", minimum = "-90", maximum = "90")
    private Double latitude;
    @Schema(description = "경도(-180~180)", example = "128.5912", minimum = "-180", maximum = "180")
    private Double longitude;
}
