package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.history.HistoryRequestDTO;
import life.eventory.activity.dto.history.HistoryResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "History API", description = "히스토리 정보 관련 API")
@RequestMapping("/api/activity")
public interface HistoryAPI {
    @Operation(summary = "행사 상세 조회 시 히스토리 기록 생성",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HistoryRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "히스토리 기록 생성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HistoryResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/history/add")
    ResponseEntity<HistoryResponseDTO> saveHistory(
            @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "히스토리 생성 객체")
            @RequestBody HistoryRequestDTO requestDTO);

    @Operation(summary = "행사 상세 조회 시 히스토리 목록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "히스토리 목록 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = HistoryResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/history/list")
    ResponseEntity<List<HistoryResponseDTO>> getHistoryList(
            @RequestHeader("X-User-Id") Long userId, @PageableDefault(sort = "viewedAt", direction = Sort.Direction.DESC) Pageable pageable);
}
