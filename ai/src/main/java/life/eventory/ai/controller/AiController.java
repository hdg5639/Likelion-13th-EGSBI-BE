package life.eventory.ai.controller;

import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController implements AiApi {
    private final AiService aiService;

    @Override
    public ResponseEntity<String> createEventSummary(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiService.createEventSummary(eventId));
    }

    @Override
    public ResponseEntity<String> createEventDescription(@RequestBody AiEventDTO aiEventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiService.createDescription(aiEventDTO));
    }
}
