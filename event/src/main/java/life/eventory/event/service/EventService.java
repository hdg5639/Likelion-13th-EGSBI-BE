package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    EventDTO createEvent(NewEventDTO newEventDTO, MultipartFile image) throws IOException;
    List<EventDTO> findAllByOrganizerId(Long organizerId);
}
