package life.eventory.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Email API", description = "이메일 인증 및 알림 관련 API")
@RequestMapping("/api/user/email")
public interface EmailAPI {
    @Operation(
            summary = "인증 코드 전송",
            parameters = @Parameter(name = "email", description = "사용자 이메일", required = true, example = "seojin57913@naver.com"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공",
                            content = @Content(schema = @Schema(type = "string", example = "인증 코드가 발송되었습니다."))),
                    @ApiResponse(responseCode = "404", description = "사용자 없음",
                            content = @Content(schema = @Schema(type = "string", example = "사용자가 존재하지 않습니다.")))
            }
    )
    @PostMapping("/send/code")
    ResponseEntity<String> sendCode(@RequestParam String email);

    @Operation(
            summary = "이메일 인증 코드 검증",
            parameters = {
                    @Parameter(name = "email", description = "사용자 이메일", required = true, example = "seojin57913@naver.com"),
                    @Parameter(name = "inputCode", description = "사용자가 입력한 인증 코드", required = true, example = "123456")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                            content = @Content(schema = @Schema(type = "string", example = "이메일 인증 성공"))),
                    @ApiResponse(responseCode = "400", description = "인증 코드가 틀렸거나 만료됨",
                            content = @Content(schema = @Schema(type = "string", example = "인증 코드가 틀렸거나 만료되었습니다.")))
            }
    )
    @PostMapping("/verify/code")
    ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code);

    @Operation(
            summary = "이메일 인증 여부 확인",
            parameters = @Parameter(name = "email", description = "사용자 이메일", required = true, example = "seojin57913@naver.com"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 여부 반환",
                            content = @Content(schema = @Schema(implementation = Boolean.class)))
            }
    )
    @GetMapping("/check/verify")
    ResponseEntity<Boolean> checkVerified(@RequestParam String email);

    @Operation(
            summary = "새 이벤트 등록 알림",
            parameters = @Parameter(name = "organizerId", description = "주최자 ID", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 발송 성공")
            }
    )
    @PostMapping("/notify/new/{organizerId}")
    ResponseEntity<Void> notifyNew(@PathVariable Long organizerId);

    @Operation(
            summary = "이벤트 수정 알림",
            parameters = @Parameter(name = "eventId", description = "이벤트 ID", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "알림 발송 성공")
            }
    )
    @PostMapping("/notify/update/{eventId}")
    ResponseEntity<Void> notifyEventUpdate(@PathVariable Long eventId);
}
