package life.eventory.event.repository;

import life.eventory.event.dto.LocationDTO;
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
    Page<Event> findAllByOrganizerIdOrderByCreateTimeDesc(Long organizerId, Pageable pageable);

    @Query("select e from Event e order by e.createTime desc")
    Page<Event> findAllByPage(Pageable pageable);

    @Query("select e from Event e where e.endTime >= CURRENT_TIMESTAMP order by e.createTime desc")
    Page<Event> findAllByPageExcludeClosed(Pageable pageable);

    @Query("""
    select e from Event e
    order by (
      6371.0 * acos(
        case
          when (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          ) > 1 then 1.0
          when (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          ) < -1 then -1.0
          else (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          )
        end
      )
    ) asc
    """)
    Page<Event> findByDistance(@Param("loc") LocationDTO loc, Pageable pageable);

    @Query("""
    select e from Event e
    where e.endTime >= CURRENT_TIMESTAMP
    order by (
      6371.0 * acos(
        case
          when (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          ) > 1 then 1.0
          when (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          ) < -1 then -1.0
          else (
            cos(radians(:#{#loc.latitude})) *
            cos(radians(e.latitude)) *
            cos(radians(e.longitude) - radians(:#{#loc.longitude})) +
            sin(radians(:#{#loc.latitude})) *
            sin(radians(e.latitude))
          )
        end
      )
    ) asc
    """)
    Page<Event> findByDistanceExcludeClosed(@Param("loc") LocationDTO loc, Pageable pageable);


    @Query("select distinct e from Event e join e.tags t where t.name in :names")
    Page<Event> findByTagNames(@Param("names") Collection<String> names, Pageable pageable);

    @Query("""
    select case when count(e) > 0 then true else false end
    from Event e
    where e.organizerId = 0 and e.name = :name
    """)
    boolean existsExternalEvent(String name);

    Boolean existsByOrganizerId(Long organizerId);
}