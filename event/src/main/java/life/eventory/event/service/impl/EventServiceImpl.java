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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CommunicationService communicationService;

    // 파일 존재 유무 판별 로직
    private boolean hasFile(MultipartFile f) {
        return f != null && !f.isEmpty()
                && f.getSize() > 0
                && StringUtils.hasText(f.getOriginalFilename());
    }

    @Override
    public EventDTO createEvent(NewEventDTO newEventDTO,  MultipartFile image) throws IOException {
        Long imageId = null;
        if (hasFile(image)) {
            imageId = communicationService.uploadPoster(image);
        }

        try {
            return entityToDTO(
                    eventRepository.save(
                            newEventDTOToEntity(newEventDTO, imageId)
                    )
            );
        }  catch (Exception e) {
            communicationService.deletePoster(imageId);
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public List<EventDTO> findAllByOrganizerId(Long organizerId) {
        return eventRepository.findAllByOrganizerId(organizerId);
    }

    @Override
    public EventDTO updateEvent(EventDTO eventDTO, MultipartFile image) throws IOException {
        Event event = eventRepository.findById(eventDTO.getId()).orElseThrow(() -> new IllegalStateException("Event not found"));

        Long oldImageId = event.getPosterId();
        // 혹시 모를 posterId 변조값 방지
        eventDTO.setPosterId(oldImageId);
        Long newImageId = null;

        // 이미지 파일이 있을 경우 업로드
        if (hasFile(image)) {
            newImageId = communicationService.uploadPoster(image);
        }

        // 이미지 ID 설정
        if (newImageId != null) {
            event.setPosterId(newImageId);
        }

        try {
            // 이전 이미지가 있으면 삭제
            if (oldImageId != null) {
                communicationService.deletePoster(oldImageId);
            }
            return entityToDTO(eventRepository.save(eventUpdate(eventDTO, event)));
        } catch (Exception e) {
            // 오류 발생 시 새로운 이미지 삭제
            if (newImageId != null) {
                communicationService.deletePoster(newImageId);
            }
            throw new IllegalStateException(e.getMessage());
        }
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

    private Event eventUpdate(EventDTO eventDTO, Event event) {
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setAddress(eventDTO.getAddress());
        event.setLatitude(eventDTO.getLatitude());
        event.setLongitude(eventDTO.getLongitude());
        event.setEntryFee(eventDTO.getEntryFee());
        return event;
    }
}
