package life.eventory.event.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.event.dto.*;
import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
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
            @RequestHeader(name = "X-User-Id", required = false) Long userId,
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
}
