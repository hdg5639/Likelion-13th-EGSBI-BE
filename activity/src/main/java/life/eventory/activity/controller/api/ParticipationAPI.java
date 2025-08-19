package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.participation.ParticipationRequestDTO;
import life.eventory.activity.dto.participation.ParticipationResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Participation API", description = "행사 참여 관련 API")
@RequestMapping("api/activity")
public interface ParticipationAPI {
    @Operation(summary = "행사 참여 처리",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ParticipationRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "행사 참여 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ParticipationResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/participation/join")
    ResponseEntity<ParticipationResponseDTO> joinEvent(@RequestHeader("X-User-Id") Long userId, @RequestBody ParticipationRequestDTO requestDTO);

    @Operation(summary = "참여 행사 리스트 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "참여 행사 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =@ArraySchema(schema = @Schema(implementation = ParticipationResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/participation/list")
    ResponseEntity<List<ParticipationResponseDTO>> getParticipations(@RequestHeader("X-User-Id") Long userId);
}
