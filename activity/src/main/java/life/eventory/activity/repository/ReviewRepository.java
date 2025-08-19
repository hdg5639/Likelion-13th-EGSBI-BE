package life.eventory.activity.repository;

import life.eventory.activity.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<ReviewEntity> findAllByEventIdOrderByCreatedAtDesc(Long eventId);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.userId =:userId")
    Double findAvgRatingByUserId(@Param("userId") Long userId);

}
