package life.eventory.event.repository;

import life.eventory.event.entity.Event;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrganizerIdOrderByCreateTimeAsc(Long organizerId);

    @Query("select distinct e from Event e join e.tags t where t.name in :names")
    Page<Event> findByTagNames(@Param("names") Collection<String> names, Pageable pageable);
}