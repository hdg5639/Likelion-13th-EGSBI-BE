package life.eventory.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.HistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "History API", description = "히스토리 정보 관련 API")
@RequestMapping("api/activity")
public interface HistoryAPI {
    @Operation(summary = "행사 상세 조회 시 히스토리 기록 생성",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HistoryDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "히스토리 기록 생성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HistoryDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/history/add")
    ResponseEntity<HistoryDTO> saveHistory(@RequestBody HistoryDTO history);

    @Operation(summary = "행사 상세 조회 시 히스토리 목록 조회",
            parameters = @Parameter(name = "userId", description = "사용자 ID", required = true, example = "100"),

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "히스토리 목록 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HistoryDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/history/list")
    ResponseEntity<Page<HistoryDTO>> getHistoryList(@RequestParam Long userId, @PageableDefault(sort = "viewedAt", direction = Sort.Direction.DESC) Pageable pageable);
}
