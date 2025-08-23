package life.eventory.ai.controller;

import life.eventory.ai.dto.AiComment;
import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.CreatedEventDTO;
import life.eventory.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AiController implements AiApi {
    private final AiService aiService;

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
    public ResponseEntity<String> createComment(@RequestBody AiComment prompt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aiService.createComment(prompt.getPrompt()));
    }

    @Override
    public ResponseEntity<String> createReviewSummary(
            @RequestHeader(name = "X-User-Id") Long userId) {
        return ResponseEntity.ok(aiService.createUserReviewSummary(userId));
    }
}
