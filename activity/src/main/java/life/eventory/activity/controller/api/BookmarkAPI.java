package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import life.eventory.activity.dto.bookmark.BookmarkRequestDTO;
import life.eventory.activity.dto.bookmark.BookmarkResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Tag(name = "Bookmark API", description = "북마크 정보 관련 API")
@RequestMapping("/api/activity")
public interface BookmarkAPI {
    @Operation(summary = "북마크 설정, 해제",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookmarkRequestDTO.class)
                    )
            ),
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "북마크 추가/ 해제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class)
                    )
            ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/bookmark/toggle")
    ResponseEntity<String> toggleBookmark(@RequestHeader("X-User-Id") Long userId, @RequestBody BookmarkRequestDTO requestDTO);

    @Operation(summary = "북마크 리스트 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "북마크 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BookmarkResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/bookmark/list")
    ResponseEntity<List<BookmarkResponseDTO>> listBookmark(@RequestHeader("X-User-Id") Long userId);

    @Operation(summary = "특정 행사 북마크 횟수 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "북마크 횟수 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/bookmark/count")
    ResponseEntity<Long> getBookmarkCount(@RequestParam Long eventId);

    @Operation(summary = "북마크 행사 전체 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "북마크 횟수 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = BookmarkResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/bookmark/all")
    ResponseEntity<LinkedHashMap<Long, Long>> getBookmarkedEvents();
}
