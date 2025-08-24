package life.eventory.activity.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.activity.dto.review.DetailReview;
import life.eventory.activity.dto.review.ReviewRequestDTO;
import life.eventory.activity.dto.review.ReviewResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Review API", description = "리뷰 관련 API")
@RequestMapping("/api/activity")
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
    ResponseEntity<Double> getAvgRatingByUser(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                              @Parameter(description = "타겟 ID (없어도 되는데, 넣으면 여기 들어간 ID로 조회됨)", example = "1")
                                              @RequestParam(required = false) Long targetId);

    @Operation(summary = "사용자별 리뷰 내용 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자별 리뷰 내용 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/review/all")
    ResponseEntity<List<String>> getUserReviews (@RequestHeader(name = "X-User-Id", required = false) Long userId,
                                                 @Parameter(description = "타겟 ID (없어도 되는데, 넣으면 여기 들어간 ID로 조회됨)", example = "1")
                                                 @RequestParam(required = false) Long targetId);

    @Operation(summary = "사용자별 리뷰 내용 조회 (상세)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자별 상세 리뷰 내용 리스트 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = DetailReview.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/review/all/detail")
    ResponseEntity<List<DetailReview>> getUserDetailReviews (@RequestHeader(name = "X-User-Id", required = false) Long userId,
                                                             @Parameter(description = "타겟 ID (없어도 되는데, 넣으면 여기 들어간 ID로 조회됨)", example = "1")
                                                             @RequestParam(required = false) Long targetId);
}