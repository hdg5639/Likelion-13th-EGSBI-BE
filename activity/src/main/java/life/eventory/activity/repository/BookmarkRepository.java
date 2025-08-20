package life.eventory.activity.repository;

import life.eventory.activity.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {
    List<BookmarkEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<BookmarkEntity> findByUserIdAndEventId(Long userId, Long eventId);
    long countByEventId(Long eventId);

    @Query("SELECT b.userId FROM BookmarkEntity b WHERE b.eventId =:eventId")
    List<Long> findUserIdsByEventId(@Param("eventId") Long eventId);
    List<BookmarkEntity> findAllByOrderByBookmarkCountDesc();
}
