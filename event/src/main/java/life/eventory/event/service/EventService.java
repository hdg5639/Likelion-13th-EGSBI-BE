package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.LocationDTO;
import life.eventory.event.dto.NewEventDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    EventDTO createEvent(NewEventDTO newEventDTO, MultipartFile image) throws IOException;
    List<EventDTO> findAllByOrganizerId(Long organizerId);
    EventDTO updateEvent(EventDTO eventDTO,  MultipartFile image) throws IOException;
    List<EventDTO> getEventPage(Integer page, Integer size, Boolean deadline);
    List<EventDTO> getEventPage(Integer page, Integer size, LocationDTO locationDTO, Boolean deadline);
    Boolean existEvent(Long eventId);
    EventDTO getEventById(Long eventId);
}
