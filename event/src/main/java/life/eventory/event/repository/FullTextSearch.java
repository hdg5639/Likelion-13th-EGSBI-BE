package life.eventory.event.repository;

import life.eventory.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FullTextSearch extends JpaRepository<Event, Long> {
    @Query(
            value = """
              SELECT e.id
              FROM t_event e
              WHERE MATCH(e.name, e.description, e.address)
                    AGAINST (:booleanQuery IN BOOLEAN MODE)
              ORDER BY MATCH(e.name, e.description, e.address)
                       AGAINST (:booleanQuery IN BOOLEAN MODE) DESC,
                       e.start_time DESC
              """,
            countQuery = """
              SELECT COUNT(*)
              FROM t_event e
              WHERE MATCH(e.name, e.description, e.address)
                    AGAINST (:booleanQuery IN BOOLEAN MODE)
              """,
            nativeQuery = true
    )
    Page<Long> searchFulltextIds(@Param("booleanQuery") String booleanQuery, Pageable pageable);

}
