package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.UserEventDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
@Tag(name = "UserEvent API", description = "행사 리스트 조회 관련 API")
@RequestMapping("api/activity")
public interface UserEventAPI {
    @Operation(summary = "사용자 행사 리스트 조회",
            parameters = @Parameter(name = "userId", description = "사용자 ID", required = true, example = "100"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "행사 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserEventDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )

    @GetMapping(value = "/userevent/{userId}")
    ResponseEntity<List<UserEventDTO>> getUserEvents(@PathVariable Long userId);

    @Operation(summary = "여러 사용자 행사 리스트 일괄 조회",
            parameters = @Parameter(name = "userId", description = "사용자 ID", required = true, example = "100"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "행사 리스트 일괄 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserEventDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/userevent/list")
    ResponseEntity<Map<Long, List<UserEventDTO>>> getAllEvents(@RequestParam List<Long> userId);
}
