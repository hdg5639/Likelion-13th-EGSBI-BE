package life.eventory.ai.service;

import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.CreatedEventDTO;

public interface AiService {
    String createEventSummary(Long userId, Long eventId);
    CreatedEventDTO createDescription(AiEventDTO aiEventDTO);
    String createComment(String prompt);
}
