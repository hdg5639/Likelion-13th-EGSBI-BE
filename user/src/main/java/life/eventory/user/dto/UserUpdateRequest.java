package life.eventory.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 정보 수정 요청 DTO")
public class UserUpdateRequest {
    @Schema(description = "이메일 주소", example = "red@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "87654321")
    private String password;

    @Schema(description = "사용자 닉네임", example = "Red_RoadEast")
    private String nickname;

    @Schema(description = "프로필 사진 유무 (삭제 시 false, 유지 혹은 변경 시 true", example = "true")
    private Boolean profileEnabled;
}
