package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 위치 응답 DTO")
public class UserLocationResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "위도 (-90 ~ 90)", example = "35.8704", minimum = "-90", maximum = "90")
    private Double latitude;

    @Schema(description = "경도 (-180 ~ 180)", example = "128.5912", minimum = "-180", maximum = "180")
    private Double longitude;

    @Schema(description = "주소", example = "대구 수성구 국채보상로 123")
    private String address;
}
