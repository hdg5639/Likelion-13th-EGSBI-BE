package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;

import java.util.List;

public interface EventService {
    EventDTO createEvent(NewEventDTO newEventDTO);
    List<EventDTO> findAllByOrganizerId(Long organizerId);
}
