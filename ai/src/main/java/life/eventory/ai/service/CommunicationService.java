package life.eventory.ai.service;

import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.dto.activity.BookmarkResponse;
import life.eventory.ai.dto.activity.HistoryRequest;
import life.eventory.ai.dto.activity.HistoryResponse;
import life.eventory.ai.dto.activity.ParticipationResponse;

import java.util.List;
import java.util.Set;

public interface CommunicationService {
    EventDTO getEvent(Long eventId);
    void addHistory(Long userId, HistoryRequest historyRequest);
    List<HistoryResponse> getHistory(Long userId);
    List<BookmarkResponse> getBookmark(Long userId);
    List<ParticipationResponse> getParticipation(Long userId);
    Set<String> getHashtags(Set<Long> eventIds);
}
