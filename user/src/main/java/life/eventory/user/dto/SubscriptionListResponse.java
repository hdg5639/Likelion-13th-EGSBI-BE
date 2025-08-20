package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "구독한 주최자 정보 응답 DTO")
public class SubscriptionListResponse {
    @Schema(description = "주최자 ID", example = "1")
    private Long organizerId;

    @Schema(description = "주최자 이름", example = "홍길동")
    private String organizerName;

    @Schema(description = "주최자 닉네임", example = "Red_RoadEast")
    private String organizerNickname;

    @Schema(description = "주최자 프로필 이미지 ID", example = "11")
    private Long profileImageId;

    @Schema(description = "주최자 프로필 이미지 URI", example = "http://image-server:8080/api/image/11")
    private String profileImageUri;
}
