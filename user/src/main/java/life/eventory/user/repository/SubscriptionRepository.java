package life.eventory.user.repository;

import life.eventory.user.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByUserIdAndOrganizerId(Long userId, Long organizerId);
    List<SubscriptionEntity> findByUserId(Long userId);
}
