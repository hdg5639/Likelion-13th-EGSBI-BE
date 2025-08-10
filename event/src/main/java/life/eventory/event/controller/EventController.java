package life.eventory.event.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.event.dto.CreateEventRequest;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Event API", description = "행사 업로드 및 조회 API")
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

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
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventDTO> createEvent(
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
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEventDTO, image));
    }

    @GetMapping("/{organizerId}")
    public ResponseEntity<List<EventDTO>> findAllByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.findAllByOrganizerId(organizerId));
    }
}