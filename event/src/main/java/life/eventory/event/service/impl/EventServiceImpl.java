package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.entity.Event;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;


    @Override
    public EventDTO createEvent(NewEventDTO newEventDTO) {
        return entityToDTO(
                eventRepository.save(
                        newEventDTOToEntity(newEventDTO)
                )
        );
    }

    @Override
    public List<EventDTO> findAllByOrganizerId(Long organizerId) {
        return eventRepository.findAllByOrganizerId(organizerId);
    }

    private Event newEventDTOToEntity(NewEventDTO newEventDTO) {
        return Event.builder()
                .organizerId(newEventDTO.getOrganizerId())
                .name(newEventDTO.getName())
                .description(newEventDTO.getDescription())
                .startTime(newEventDTO.getStartTime())
                .endTime(newEventDTO.getEndTime())
                .address(newEventDTO.getAddress())
                .latitude(newEventDTO.getLatitude())
                .longitude(newEventDTO.getLongitude())
                .entryFee(newEventDTO.getEntryFee())
                .build();
    }

    private EventDTO entityToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .organizerId(event.getOrganizerId())
                .name(event.getName())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .address(event.getAddress())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .entryFee(event.getEntryFee())
                .build();
    }
}
