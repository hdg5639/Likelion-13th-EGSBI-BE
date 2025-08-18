package life.eventory.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.user.dto.SubscriptionCreateRequest;
import life.eventory.user.entity.SubscriptionEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subscription API", description = "구독 관련 API")
@RequestMapping("/api/user/subscription")
public interface SubscriptionAPI {

    @Operation(
            summary = "구독 생성",
            description = "사용자 ID를 헤더에서 받아 구독 정보를 생성합니다.",
            requestBody = @RequestBody(
                    description = "구독 생성 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubscriptionCreateRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "구독 생성 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SubscriptionCreateRequest.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/create")
    ResponseEntity<SubscriptionCreateRequest> createSubscription(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody SubscriptionCreateRequest request);

    @Operation(
            summary = "사용자 구독 전체 조회",
            description = "사용자 ID를 헤더에서 받아 해당 사용자의 모든 구독 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SubscriptionEntity.class))),
                    @ApiResponse(responseCode = "204", description = "구독 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/getAll")
    ResponseEntity<List<SubscriptionEntity>> getAllSubscriptions(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestHeader("X-User-Id") Long userId);

    @Operation(
            summary = "구독 삭제",
            description = "사용자 ID를 헤더에서 받아 구독 정보를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "삭제 성공")
                            )),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @DeleteMapping("/delete")
    ResponseEntity<String> deleteSubscription(
            @Parameter(description = "사용자 ID", example = "1")
            @RequestHeader("X-User-Id") Long userId
    );
}
