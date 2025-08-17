package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import life.eventory.activity.dto.BookmarkDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Bookmark API", description = "북마크 정보 관련 API")
@RequestMapping("api/activity")
public interface BookmarkAPI {
    @Operation(summary = "북마크 설정, 해제",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookmarkDTO.class)
                    )
            ),
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "북마크 추가/ 해제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookmarkDTO.class)
                    )
            ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/bookmark/toggle")
    ResponseEntity<String> toggleBookmark(@RequestBody BookmarkDTO bookmarkDTO);

    @Operation(summary = "북마크 리스트 조회",
            parameters = @Parameter(name = "userId", description = "사용자 ID", required = true, example = "100"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "북마크 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookmarkDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/bookmark/list")
    ResponseEntity<List<BookmarkDTO>> listBookmark(@RequestParam Long userId);
}
