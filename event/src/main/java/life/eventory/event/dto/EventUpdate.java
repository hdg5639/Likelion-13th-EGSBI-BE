package life.eventory.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventUpdate", description = "행사 업데이트 DTO")
public class EventUpdate {
    @Schema(description = "행사 식별자", example = "123")
    private Long id;

    @Schema(description = "행사명", example = "수성구 야시장")
    private String name;

    @Schema(description = "포스터 유무 (삭제 시: false, 유지 or 변경 시: true)", example = "true")
    private Boolean poster;

    @Schema(description = "행사 상세 설명", example = "푸드트럭, 라이브 공연 등 다양한 볼거리와 먹거리가 제공됩니다.")
    private String description;

    @Schema(
            description = "행사 시작 시각 (KST 기준 예시)",
            type = "string",
            format = "date-time",
            example = "2025-08-31T19:30:00"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(
            description = "행사 종료 시각 (KST 기준 예시)",
            type = "string",
            format = "date-time",
            example = "2025-08-31T21:30:00"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "행사 장소 주소", example = "대구 수성구 국채보상로 123")
    private String address;

    @Schema(description = "위도(-90~90)", example = "35.8704", minimum = "-90", maximum = "90")
    private Double latitude;

    @Schema(description = "경도(-180~180)", example = "128.5912", minimum = "-180", maximum = "180")
    private Double longitude;

    @Schema(description = "입장료(원). 무료일 경우 0", example = "20000", minimum = "0")
    private Integer entryFee;

    @Schema(
            description = "생성 시각",
            type = "string",
            format = "date-time",
            example = "2025-08-10T10:15:30",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "QR 이미지 ID(없을 수 있음)", example = "1001")
    private Long qrImage;

    @Schema(description = "해시태그 목록", example = "[\"야시장\",\"음악\",\"대구축제\"]")
    private List<String> hashtags;
}
