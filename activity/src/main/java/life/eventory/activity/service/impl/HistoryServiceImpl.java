package life.eventory.activity.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.activity.dto.HistoryDTO;
import life.eventory.activity.entity.HistoryEntity;
import life.eventory.activity.repository.HistoryRepository;
import life.eventory.activity.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepository historyRepository;
//    private final UserRepository userRepository;
//    private final EventRepository eventRepository;
    // 서버 간 통신 받아와야 함

    @Override
    @Transactional
    public HistoryDTO recordView(HistoryDTO historyDTO) {
        Long userId = historyDTO.getUserId();
        Long eventId = historyDTO.getEventId();

//        // 사용자 존재 여부 검증
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 행사 존재 여부 검증
//        EventEntity event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 48시간 전 시간 계산(중복 기록 방지)
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusHours(48);

        Optional<HistoryEntity> recentHistory = historyRepository
                .findByHistory(userId, eventId, twoDaysAgo);

        if (recentHistory.isPresent()) {
            // 기존 조회 기록이 있을 때 조회 시간만 갱신 후 저장
            HistoryEntity history = recentHistory.get();
            history.setViewedAt(LocalDateTime.now());  // 조회 시간 갱신
            return historyRepository.save(history).toDTO();
        }
        else {
            // 새 조회 기록 생성 후 저장
            HistoryEntity newHistory = HistoryEntity.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .viewedAt(LocalDateTime.now())
                    .build();
            return historyRepository.save(newHistory).toDTO();
        }
    }

    // 특정 사용자의 조회 히스토리 리스트를 페이징, 최신순으로 정렬
    @Override
    public Page<HistoryDTO> getHistoryList(Long userId, Pageable pageable) {
        Page<HistoryEntity> history = historyRepository.findByUserId(userId, pageable);
        return history.map(HistoryEntity::toDTO);
    }
}
