package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.review.ReviewRequestDTO;
import life.eventory.activity.dto.review.ReviewResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Review API", description = "리뷰 관련 API")
@RequestMapping("api/activity")
public interface ReviewAPI {
    @Operation(summary = "사용자별 참여 행사 리뷰 작성 생성",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ReviewRequestDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "참여 행사 리뷰 작성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ReviewResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(value = "/review/{eventId}")
    ResponseEntity<?> createReview(@RequestHeader("X-User-Id") Long userId,
                                                   @PathVariable Long eventId,
                                                   @RequestBody ReviewRequestDTO requestDTO);

    @Operation(summary = "한 행사의 모든 리뷰 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "행사 리뷰 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array =@ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/review/eventlist")
    ResponseEntity<List<ReviewResponseDTO>> getReviewsByEvent(@RequestParam Long eventId);

    @Operation(summary = "사용자별 평균 평점 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자 평균 평점 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Double.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping(value = "/review/rating")
    ResponseEntity<Double> getAvgRatingByUser(@RequestHeader("X-User-Id") Long userId);

}