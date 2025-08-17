package life.eventory.activity.repository;

import life.eventory.activity.entity.UserEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventRepository extends JpaRepository<UserEventEntity, Long> {
    List<UserEventEntity> findByUserId(Long userId);
    List<UserEventEntity> findByUserIdIn(List<Long> userId);
}
