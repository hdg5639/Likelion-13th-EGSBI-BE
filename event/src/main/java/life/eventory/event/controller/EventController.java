package life.eventory.event.controller;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody NewEventDTO newEventDTO) {
        return ResponseEntity.ok(eventService.createEvent(newEventDTO));
    }
    @GetMapping("/{organizerId}")
    public ResponseEntity<List<EventDTO>> findAllByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(eventService.findAllByOrganizerId(organizerId));
    }
}
