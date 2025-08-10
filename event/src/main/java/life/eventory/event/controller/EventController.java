package life.eventory.event.controller;

import life.eventory.event.dto.EventDTO;
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
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<EventDTO> createEvent(
            @RequestPart("event") NewEventDTO newEventDTO,
            @RequestPart("image") MultipartFile image) throws IOException{
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(newEventDTO, image));
    }

    @GetMapping("/{organizerId}")
    public ResponseEntity<List<EventDTO>> findAllByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.findAllByOrganizerId(organizerId));
    }
}