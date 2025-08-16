package life.eventory.ai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "AiEventDTO", description = "AI 행사 생성 정보 DTO")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiEventDTO {
    @Schema(description = "행사명", example = "행사 이름 작성")
    private String name;

    @Schema(description = "AI 생성용 행사 간단 설명", example = "간단한 행사 내용 작성")
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

    @Schema(description = "입장료(원). 무료일 경우 0", example = "20000", minimum = "0")
    private Integer entryFee;

    @Schema(description = "해시태그 목록", example = "[\"야시장\",\"음악\",\"대구축제\"]")
    private List<String> hashtags;
}
