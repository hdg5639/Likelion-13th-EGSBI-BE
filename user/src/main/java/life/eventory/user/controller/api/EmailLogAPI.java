package life.eventory.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.user.entity.EmailLogEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Email Log API", description = "이메일 발송 로그 관련 API")
@RequestMapping("/api/user/email_log")
public interface EmailLogAPI {

    @Operation(
            summary = "이메일로 발송 로그 조회",
            parameters = @Parameter(name = "email", description = "조회할 사용자 이메일", required = true, example = "seojin57913@naver.com"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일 로그 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmailLogEntity.class))),
                    @ApiResponse(responseCode = "204", description = "로그 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/getByEmail")
    ResponseEntity<List<EmailLogEntity>> getLogsByEmail(@RequestParam String email);
}
