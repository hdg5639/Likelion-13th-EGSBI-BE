package life.eventory.user.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 성공 반환 값")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "엑세스 토큰", accessMode = Schema.AccessMode.READ_ONLY)
    private String accessToken;
    @Schema(description = "만료 기간 (단위: 초)", example = "86400", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer expiresIn;
    @Schema(description = "토큰 타입", example = "Bearer", accessMode = Schema.AccessMode.READ_ONLY)
    private String tokenType;
}
