package life.eventory.event.repository;

import life.eventory.event.entity.Event;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByOrganizerIdOrderByCreateTimeDesc(Long organizerId, Pageable pageable);

    @Query("""
    select case when count(e) > 0 then true else false end
    from Event e
    where e.organizerId = 0 and e.name = :name
    """)
    boolean existsExternalEvent(String name);

    Boolean existsByOrganizerId(Long organizerId);

    @Query("select (count(e) > 0) from Event e where e.id = :eventId and e.qrImage is not null")
    boolean hasQrImage(@Param("eventId") Long eventId);

    @Query("select e.qrImage from Event e where e.id = :eventId")
    Optional<Long> findQrImageById(@Param("eventId") Long eventId);

    @Query("""
        select distinct e
        from Event e
        left join fetch e.tags
        where e.id in :ids
        """)
    List<Event> findAllWithTagsByIdIn(@Param("ids") Set<Long> ids);

    @Query("""
        select distinct e.id
        from Event e
        join e.tags t
        where t.id in :topTagIds
          and e.endTime >= :now
          and (:excludeIds is null or e.id not in :excludeIds)
        """)
    List<Long> findCandidateIds(
            @Param("topTagIds") Set<Long> topTagIds,
            @Param("excludeIds") Set<Long> excludeIds,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        select distinct e
        from Event e
        left join fetch e.tags
        where e.id in :ids
        """)
    List<Event> findAllWithTagsByIdInOrderAgnostic(@Param("ids") List<Long> ids);
}