package life.eventory.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "신규 이벤트 생성 요청 DTO")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDTO {

    @Schema(description = "주최자 ID", example = "1")
    private Long organizerId;

    @Schema(description = "이벤트 이름", example = "여름 음악 페스티벌")
    private String name;

    @Schema(description = "이벤트 설명", example = "여름밤을 수놓는 라이브 음악 공연")
    private String description;

    @Schema(description = "이벤트 시작 시간 (ISO 8601 형식)", example = "2025-08-15T18:00:00")
    private LocalDateTime startTime;

    @Schema(description = "이벤트 종료 시간 (ISO 8601 형식)", example = "2025-08-15T21:00:00")
    private LocalDateTime endTime;

    @Schema(description = "이벤트 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "참가비 (원)", example = "10000")
    private Integer entryFee;
}
