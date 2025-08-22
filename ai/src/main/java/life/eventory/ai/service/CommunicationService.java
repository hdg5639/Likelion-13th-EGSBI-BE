package life.eventory.ai.service;

import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.dto.activity.HistoryRequest;

import java.util.List;

public interface CommunicationService {
    EventDTO getEvent(Long eventId);
    void addHistory(Long userId, HistoryRequest historyRequest);
    List<String> getReviews(Long userId);
}
