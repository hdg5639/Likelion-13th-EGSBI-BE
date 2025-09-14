package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.notification.NotificationRequestDTO;
import life.eventory.activity.dto.notification.NotificationResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification API", description = "알림 관련 API")
@RequestMapping("/v1/activity")
public interface NotificationAPI {
    @Operation(summary = "알림 설정, 해제",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "알림 추가/ 해제 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/notification/toggle")
    ResponseEntity<String> toggleNotification(@RequestHeader("X-User-Id") Long userId,
                                              @RequestBody NotificationRequestDTO requestDTO);

    @Operation(summary = "알림 리스트 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "알림 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/notification/list")
    ResponseEntity<List<NotificationResponseDTO>> notificationList(@RequestHeader("X-User-Id") Long userId);
}
