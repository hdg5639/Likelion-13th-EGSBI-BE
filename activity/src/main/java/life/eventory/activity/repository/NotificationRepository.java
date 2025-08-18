package life.eventory.activity.repository;

import life.eventory.activity.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByUserIdAndEventId(Long userId, Long eventId);
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

}
