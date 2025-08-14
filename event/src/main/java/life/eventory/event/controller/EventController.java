package life.eventory.event.controller;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.LocationDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {
    private final EventService eventService;

    @Override
    public ResponseEntity<EventDTO> createEvent(
            @RequestPart(value = "event") NewEventDTO newEventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEventDTO, image));
    }

    @Override
    public ResponseEntity<List<EventDTO>> findAllByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.findAllByOrganizerId(organizerId));
    }

    @Override
    public ResponseEntity<EventDTO> updateEvent(
            @RequestPart(value = "event") EventDTO eventDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(eventService.updateEvent(eventDTO, image));
    }

    // TODO: 최신순 / 거리순 / 마감 제외 / 마감 포함 / 인기순
    // 전체 조회
    @Override
    public ResponseEntity<List<EventDTO>> getEventsByLocationPage(@RequestParam Integer page,
                                                                  @RequestParam Integer size,
                                                                  @RequestParam Boolean deadline) {
        return ResponseEntity.ok(eventService.getEventPage(page, size, deadline));
    }

    @Override
    public ResponseEntity<List<EventDTO>> getEventsByLocationPage(@RequestParam Integer page,
                                                                  @RequestParam Integer size,
                                                                  @RequestParam Boolean deadline,
                                                                  @RequestParam Double latitude,
                                                                  @RequestParam Double longitude) {
        return ResponseEntity.ok(eventService.getEventPage(page, size, new LocationDTO(latitude, longitude), deadline));
    }
}