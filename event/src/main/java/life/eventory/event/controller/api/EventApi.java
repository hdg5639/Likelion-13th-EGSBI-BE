package life.eventory.event.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.event.dto.*;
import life.eventory.event.dto.activity.EventBookmark;
import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
import life.eventory.event.dto.recommender.AiRecommendResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Event API", description = "행사 업로드 및 조회 API")
@RequestMapping("/api/event")
public interface EventApi {
    @Operation(summary = "행사 생성",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = CreateEventRequest.class),
                            encoding = {
                                    @Encoding(name = "event", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청(필드 검증 실패/누락)", content = @Content),
                    @ApiResponse(responseCode = "415", description = "지원하지 않는 Content-Type", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EventDTO> createEvent(
            @RequestHeader(name = "X-User-Id") Long userId,
            @Parameter(
                    description = "이벤트 본문(JSON)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NewEventDTO.class)
                    )
            )
            @RequestPart(value = "event") NewEventDTO newEventDTO,
            @Parameter(
                    description = "포스터 이미지 파일 (선택)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException;

    @Operation(
            summary = "주최자 ID로 행사 목록 조회",
            parameters = {
                    @Parameter(name = "organizerId", description = "주최자 ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/{organizerId}")
    ResponseEntity<List<EventDTO>> findAllByOrganizerId(
            @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable,
            @PathVariable Long organizerId);

    @Operation(summary = "행사 수정",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UpdateEventRequest.class),
                            encoding = {
                                    @Encoding(name = "event", contentType = "application/json"),
                                    @Encoding(name = "image", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = EventDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청(필드 검증 실패/누락)", content = @Content),
                    @ApiResponse(responseCode = "415", description = "지원하지 않는 Content-Type", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EventDTO> updateEvent(
            @RequestHeader(name = "X-User-Id") Long userId,
            @RequestPart(value = "event") EventUpdate eventUpdate,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException;

    @Operation(
            summary = "이벤트 전체 조회 (페이징, 날짜 최신순 자동 정렬)",
            parameters = {
                    @Parameter(name = "deadline", description = "마감일 포함 토글 (포함: true, 제외: false)", required = true, example = "true")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping
    ResponseEntity<List<EventDTO>> getEventsPage(
            @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable,
            @Parameter(description = "마감일 포함 토글 (포함: true, 제외: false)", example = "true")
            @RequestParam Boolean deadline);

    @Operation(
            summary = "이벤트 거리순 조회 (페이징, 거리순 자동 정렬)",
            parameters = {
                    @Parameter(name = "deadline", description = "마감일 포함 토글 (포함: true, 제외: false)", required = true, example = "true"),
                    @Parameter(name = "latitude", description = "사용자 위치 (위도)", required = true, example = "35.8704"),
                    @Parameter(name = "longitude", description = "사용자 위치 (경도)", required = true, example = "128.5912")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/loc")
    ResponseEntity<List<EventDTO>> getEventsPage(
            @ParameterObject Pageable pageable,
            @Parameter(description = "마감일 포함 토글 (포함: true, 제외: false)", example = "true")
            @RequestParam Boolean deadline,
            @Parameter(description = "사용자 위치 (위도)")
            @RequestParam Double latitude,
            @Parameter(description = "사용자 위치 (경도)")
            @RequestParam Double longitude);

    @Operation(
            summary = "행사 존재 유무 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/exist/{eventId}")
    ResponseEntity<Boolean> existEvent(
            @Parameter(description = "행사 ID", example = "1")
            @PathVariable Long eventId);

    @Operation(
            summary = "행사 단건 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = EventDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/info/{eventId}")
    ResponseEntity<EventDTO> getEventById(
            @Parameter(description = "행사 ID", example = "1")
            @PathVariable Long eventId);

    @Operation(
            summary = "AI 행사 본문, 해시태그 생성",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AiEventDTO.class),
                            encoding = {
                                    @Encoding(contentType = "application/json")
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = CreatedEventInfoDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/ai/description")
    ResponseEntity<CreatedEventInfoDTO> createAiEvent(
            @Parameter(description = "AI 행사 생성 객체")
            @RequestBody AiEventDTO aiEventDTO);

    @Operation(
            summary = "행사 주최자 유무 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/exist/organizer/{organizerId}")
    ResponseEntity<Boolean> existOrganizer(
            @Parameter(description = "주최자 ID", example = "1")
            @PathVariable Long organizerId);

    @Operation(
            summary = "AI 추천 행사 조회 (최대 상위 10개)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = AiRecommendResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/recommend")
    ResponseEntity<AiRecommendResponse> getAiRecommend(
            @RequestHeader(name = "X-User-Id") Long userId);

    @Operation(
            summary = "개인 북마크 행사 리스트 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = EventDTO.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/bookmarks")
    ResponseEntity<List<EventDTO>> getBookmarkList(
            @RequestHeader(name = "X-User-Id") Long userId);

    @Operation(
            summary = "개인 행사 조회 기록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = EventDTO.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/histories")
    ResponseEntity<List<EventDTO>> getHistoryList(
            @RequestHeader(name = "X-User-Id") Long userId,
            Pageable pageable);

    @Operation(
            summary = "인기순 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = EventBookmark.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/popular")
    ResponseEntity<List<EventBookmark>> getBookmarkedEventsInOrder();

    @Operation(
            summary = "행사 검색 (풀텍스트, 관련도순)",
            description = """
                name/description/address 대상으로 풀텍스트 검색합니다.
                검색어는 공백으로 구분되며 내부적으로 +토큰* 형태(Boolean mode)로 변환됩니다.
                예) "AI 세미나" → "+AI* +세미나*"
                """,
            parameters = {
                    @Parameter(name = "q", description = "검색어(공백 구분)", required = true, example = "AI 세미나"),
                    @Parameter(name = "page", description = "페이지 번호(0부터)", example = "0"),
                    @Parameter(name = "size", description = "페이지 크기", example = "20"),
                    @Parameter(name = "sort", description = "정렬(미사용, 내부적으로 관련도순)", example = "startTime,desc")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/search")
    ResponseEntity<List<EventDTO>> searchFulltext(
            @Parameter(description = "검색어", required = true)
            @RequestParam String q,

            @Parameter(hidden = true) // springdoc가 page/size/sort를 자동 노출
            @PageableDefault(size = 20) Pageable pageable
    );

    @Operation(
            summary = "24시간 이내 시작 행사 리스트 조회",
            description = "24시간 이내 시작하는 행사 ID 조회"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Long.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/upcoming")
    ResponseEntity<List<Long>> getEventIdsStartingWithin24h();
}
