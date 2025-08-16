package life.eventory.event.controller;

import life.eventory.event.controller.api.EventApi;
import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.LocationDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {
    private final EventService eventService;
    private final CommunicationService communicationService;

    @Override
    public ResponseEntity<EventDTO> createEvent(
            @RequestPart(value = "event") NewEventDTO newEventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEventDTO, image));
    }

    @Override
    public ResponseEntity<List<EventDTO>> findAllByOrganizerId(
            @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable,
            @PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.findAllByOrganizerId(pageable, organizerId));
    }

    @Override
    public ResponseEntity<EventDTO> updateEvent(
            @RequestPart(value = "event") EventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(eventService.updateEvent(eventDTO, image));
    }

    // TODO: 인기순
    // 전체 조회
    @Override
    public ResponseEntity<List<EventDTO>> getEventsPage(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC)
                                                        @ParameterObject Pageable pageable,
                                                        @RequestParam Boolean deadline) {
        return ResponseEntity.ok(eventService.getEventPage(pageable, deadline));
    }

    @Override
    public ResponseEntity<List<EventDTO>> getEventsPage(@ParameterObject Pageable pageable,
                                                        @RequestParam Boolean deadline,
                                                        @RequestParam Double latitude,
                                                        @RequestParam Double longitude) {
        return ResponseEntity.ok(eventService.getEventPage(pageable, new LocationDTO(latitude, longitude), deadline));
    }

    @Override
    public ResponseEntity<Boolean> existEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.existEvent(eventId));
    }

    @Override
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @Override
    public ResponseEntity<CreatedEventInfoDTO> createAiEvent(@RequestBody AiEventDTO aiEventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communicationService.createAiDescription(aiEventDTO));
    }

    @Override
    public ResponseEntity<Boolean> existOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.existOrganizer(organizerId));
    }
}