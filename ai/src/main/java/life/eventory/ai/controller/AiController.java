package life.eventory.ai.controller;

import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.CreatedEventDTO;
import life.eventory.ai.dto.Recommender;
import life.eventory.ai.service.AiEventRecommender;
import life.eventory.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController implements AiApi {
    private final AiService aiService;
    private final AiEventRecommender aiEventRecommender;

    @Override
    public ResponseEntity<String> createEventSummary(
            @RequestHeader(name = "X-User-Id", required = false) Long userId,
            @PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiService.createEventSummary(userId, eventId));
    }

    @Override
    public ResponseEntity<CreatedEventDTO> createEventDescription(@RequestBody AiEventDTO aiEventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiService.createDescription(aiEventDTO));
    }

    @Override
    public ResponseEntity<Recommender> getRecommender(
            @RequestHeader(name = "X-User-Id") Long userId) {
        return ResponseEntity.ok(aiEventRecommender.combineInfo(userId));
    }
}
