package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 정보 응답 DTO")
public class UserInfoResponse {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "닉네임", example = "Red_RoadEast")
    private String nickname;
}
