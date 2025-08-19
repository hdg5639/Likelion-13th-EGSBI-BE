package life.eventory.ai.service;

import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.dto.activity.HistoryRequest;

public interface CommunicationService {
    EventDTO getEvent(Long eventId);
    void addHistory(Long userId, HistoryRequest historyRequest);
}
