package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.EventUpdate;
import life.eventory.event.dto.LocationDTO;
import life.eventory.event.dto.NewEventDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    EventDTO createEvent(NewEventDTO newEventDTO, MultipartFile image) throws IOException;
    List<EventDTO> findAllByOrganizerId(Pageable pageable, Long organizerId);
    EventDTO updateEvent(EventUpdate eventUpdate, MultipartFile image) throws IOException;
    List<EventDTO> getEventPage(Pageable pageable, Boolean deadline);
    List<EventDTO> getEventPage(Pageable pageable, LocationDTO locationDTO, Boolean deadline);
    Boolean existEvent(Long eventId);
    EventDTO getEventById(Long userId, Long eventId);
    Boolean existOrganizer(Long organizerId);
}
