package life.eventory.ai.service;

import life.eventory.ai.dto.EventDTO;

public interface CommunicationService {
    EventDTO getEvent(Long eventId);
}
