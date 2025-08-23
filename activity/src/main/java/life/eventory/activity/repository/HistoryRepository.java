package life.eventory.activity.repository;

import life.eventory.activity.entity.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    Page<HistoryEntity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT h FROM HistoryEntity h WHERE h.userId = :userId AND h.eventId = :eventId AND h.viewedAt >= :time")
    Optional<HistoryEntity> findByHistory(@Param("userId") Long userId, @Param("eventId") Long eventId,@Param("time") LocalDateTime time);
}
