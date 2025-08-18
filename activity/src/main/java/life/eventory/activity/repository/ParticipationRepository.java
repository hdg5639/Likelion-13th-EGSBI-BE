package life.eventory.activity.repository;

import life.eventory.activity.entity.ParticipationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {
    List<ParticipationEntity> findByUserId(Long userId);
    Optional<ParticipationEntity> findByUserIdAndEventId(Long userId, Long eventId);
}
