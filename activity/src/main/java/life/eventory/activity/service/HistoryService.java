package life.eventory.activity.service;

import life.eventory.activity.dto.history.HistoryRequestDTO;
import life.eventory.activity.dto.history.HistoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    HistoryResponseDTO recordView(Long userId, HistoryRequestDTO requestDTO);
    Page<HistoryResponseDTO> getHistoryList(Long userId, Pageable pageable);
}
