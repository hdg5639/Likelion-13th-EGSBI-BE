package life.eventory.event.service;

import life.eventory.event.dto.activity.BookmarkResponse;
import life.eventory.event.dto.activity.HistoryRequest;
import life.eventory.event.dto.activity.HistoryResponse;
import life.eventory.event.dto.activity.ParticipationResponse;
import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommunicationService {
    Long uploadPoster(MultipartFile file) throws IOException;
    void deletePoster(Long posterId);
    void existUser(Long userId);
    CreatedEventInfoDTO createAiDescription(AiEventDTO aiEventDTO);
    void addHistory(Long userId, HistoryRequest historyRequest);
    List<HistoryResponse> getHistory(Long userId);
    List<HistoryResponse> getHistoryPage(Long userId, Pageable pageable);
    List<BookmarkResponse> getBookmark(Long userId);
    List<ParticipationResponse> getParticipation(Long userId);
    String getComment(String prompt);
    List<BookmarkResponse> getAllBookmarkedEvents();
}
