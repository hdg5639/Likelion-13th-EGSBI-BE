package life.eventory.event.repository;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select new life.eventory.event.dto.EventDTO(e.id, e.organizerId, e.name,e.posterId , e.description, e.startTime, e.endTime, e.address, e.latitude, e.longitude, e.entryFee) from Event e where e.organizerId = :organizerId")
    List<EventDTO> findAllByOrganizerId(Long organizerId);
}