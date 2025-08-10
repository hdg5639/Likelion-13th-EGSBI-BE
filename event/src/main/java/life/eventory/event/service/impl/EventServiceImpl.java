package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.entity.Event;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CommunicationService communicationService;


    @Override
    public EventDTO createEvent(NewEventDTO newEventDTO,  MultipartFile image) throws IOException {
        Long imageId = null;
        if (image != null) {
            imageId = communicationService.uploadPoster(image);
        }

        return entityToDTO(
                eventRepository.save(
                        newEventDTOToEntity(newEventDTO, imageId)
                )
        );
    }

    @Override
    public List<EventDTO> findAllByOrganizerId(Long organizerId) {
        return eventRepository.findAllByOrganizerId(organizerId);
    }

    private Event newEventDTOToEntity(NewEventDTO newEventDTO, Long posterId) {
        return Event.builder()
                .organizerId(newEventDTO.getOrganizerId())
                .name(newEventDTO.getName())
                .posterId(posterId)
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
                .posterId(event.getPosterId())
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
