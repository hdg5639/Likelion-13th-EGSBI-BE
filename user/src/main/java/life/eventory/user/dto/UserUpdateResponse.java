package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 정보 수정 응답 DTO")
public class UserUpdateResponse {
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일 주소", example = "red@example.com")
    private String email;

    @Schema(description = "전화번호", example = "010-5678-1234")
    private String phone;

    @Schema(description = "사용자 닉네임", example = "Red_RoadEast")
    private String nickname;

    @Schema(description = "프로필 ID", example = "1")
    private Long profileId;
}
