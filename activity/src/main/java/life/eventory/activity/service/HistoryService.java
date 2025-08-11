package life.eventory.activity.service;

import life.eventory.activity.dto.HistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    HistoryDTO recordView(HistoryDTO historyDTO);
    Page<HistoryDTO> getHistoryList(Long userId, Pageable pageable);
}
