package life.eventory.ai.service;

import life.eventory.ai.dto.AiEventDTO;

public interface AiService {
    String createEventSummary(Long eventId);
    String createDescription(AiEventDTO aiEventDTO);
}
