package life.eventory.activity.repository;

import life.eventory.activity.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    Page<HistoryEntity> findByUserId(Long userId, Pageable pageable);
    Optional<HistoryEntity> findByHistory(Long userId, Long eventId, LocalDateTime time);
}
