package life.eventory.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.user.dto.*;
import life.eventory.user.dto.login.LoginRequest;
import life.eventory.user.dto.login.LoginResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "User API", description = "회원 가입 및 사용자 정보 관련 API")
@RequestMapping("/api/user")
public interface UserAPI {
    @Operation(
            summary = "회원가입",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "user", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserSignUpRequest.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserSignUpRequest> signup(
            @Parameter(
                    description = "회원가입 정보 (JSON)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignUpRequest.class)
                    ))
            @RequestPart(value = "user") UserSignUpRequest request,
            @Parameter(
                    description = "프로필 이미지 파일 (선택)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    ))
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException;

    @Operation(summary = "사용자 정보 조회",
            parameters = @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserInfoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/info")
    ResponseEntity<UserInfoResponse> info(@RequestParam Long userId);

    @Operation(
            summary = "회원 정보 수정",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            encoding = {
                                    @Encoding(name = "user", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 정보 수정 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserUpdateRequest.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserUpdateResponse> update(
            @Parameter(
                    description = "회원 정보 수정 데이터 (JSON)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequest.class)
                    )
            )
            @RequestPart(value = "user") UserUpdateRequest request,

            @Parameter(
                    description = "새 프로필 이미지 파일 (선택)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws IOException;


    @Operation(summary = "사용자 위치 저장",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserLocationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "위치 저장 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserLocationRequest.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/location")
    ResponseEntity<UserLocationRequest> location(@RequestBody UserLocationRequest request);

    @Operation(summary = "사용자 존재 여부 확인",
            parameters = @Parameter(name = "id", description = "사용자 ID", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 존재 여부 반환"),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/exist/{id}")
    boolean checkUserExists(@PathVariable Long id);

    @Operation(summary = "사용자 위치 정보 조회",
            parameters = @Parameter(name = "id", description = "사용자 ID", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserLocationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자 위치 정보 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/location/{id}")
    ResponseEntity<UserLocationResponse> locationInfo(@PathVariable Long id);

    @Operation(summary = "로그인",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Parameter(description = "로그인 요청 정보")
            @RequestBody LoginRequest request);

    @Operation(summary = "토큰 재발급",
            responses = {
                    @ApiResponse(responseCode = "200", description = "재발급 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/renew")
    ResponseEntity<LoginResponse> renew(@RequestHeader("X-User-Id") String userId);

    @Operation(summary = "사용자 위치 정보 삭제",
            parameters = @Parameter(name = "email", description = "사용자 이메일", required = true, example = "red@example.com"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "위치 정보 삭제 성공",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "string", example = "삭제 성공"))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "404", description = "사용자 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @DeleteMapping("/delete/location")
    ResponseEntity<String> deleteLocation(@RequestParam String email);

}
