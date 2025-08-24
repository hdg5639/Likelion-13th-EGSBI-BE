package life.eventory.activity.repository;

import life.eventory.activity.dto.review.DetailReview;
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

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.eventId in :eventIds")
    Double findAvgRatingByEventIds(@Param("eventIds") List<Long> eventIds);

    @Query("""
    select r.content
    from ReviewEntity r
    where r.eventId in :eventIds
    """)
    List<String> findReviewsByEventIds(@Param("eventIds") List<Long> eventIds);

    @Query("""
    select new life.eventory.activity.dto.review.DetailReview(r.userId, r.content, r.rating)
    from ReviewEntity r
    where r.eventId in :eventIds
    """)
    List<DetailReview> findDetailReviews(@Param("eventIds") List<Long> eventIds);
}
