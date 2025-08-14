package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.NewEventDTO;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.Tag;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.EventService;
import life.eventory.event.service.EventTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventTagService eventTagService;
    private final CommunicationService communicationService;

    // 파일 존재 유무 판별 로직
    private boolean hasFile(MultipartFile f) {
        if (f == null) return false;
        log.info("file size {}", f.getSize());
        if (f.isEmpty()) return false; // size=0 or filename empty 포함
        if (f.getSize() == 0) return false;
        log.info("file content type {}", f.getContentType());
        if (f.getContentType() == null) return false;
        log.info("file file name {}", f.getOriginalFilename());
        if (f.getOriginalFilename() == null) return false;
        return StringUtils.hasText(f.getOriginalFilename());
    }

    @Override
    public EventDTO createEvent(NewEventDTO newEventDTO,  MultipartFile image) throws IOException {
        Long imageId = null;
        boolean hasImage = hasFile(image);
        log.info("status {}", hasImage);
        if (hasImage) {
            imageId = communicationService.uploadPoster(image);
        }

        try {
            log.info("create event {}", newEventDTO);
            return entityToDTO(
                    eventTagService.setEventHashtags(
                            eventRepository.save(newEventDTOToEntity(newEventDTO, imageId)).getId(),
                            newEventDTO.getHashtags()
                )
            );
        }  catch (Exception e) {
            log.error(e.getMessage());
            if(hasImage) {
                communicationService.deletePoster(imageId);
            }
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public List<EventDTO> findAllByOrganizerId(Long organizerId) {
        return getByOrganizer(organizerId);
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
            return entityToDTO(
                    eventTagService.setEventHashtags(
                            eventRepository.save(eventUpdate(eventDTO, event)).getId(),
                            eventDTO.getHashtags()
                    )
            );
        } catch (Exception e) {
            // 오류 발생 시 새로운 이미지 삭제
            if (newImageId != null) {
                communicationService.deletePoster(newImageId);
            }
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public List<EventDTO> getEventPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Event> eventPage = eventRepository.findAllByPage(pageable);
        return eventPage.getContent().stream()
                .map(this::entityToDTO)
                .sorted(Comparator.comparing(EventDTO::getCreateTime).reversed())
                .toList();
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
                .createTime(LocalDateTime.now())
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
                .createTime(event.getCreateTime())
                .hashtags(
                        event.getTags() == null ?
                                List.of() :
                                event.getTags().stream().map(Tag::getDisplayName).toList()
                )
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

    private List<EventDTO> getByOrganizer(Long organizerId) {
        List<Event> events = eventRepository.findAllByOrganizerIdOrderByCreateTimeAsc(organizerId);
        return events.stream().map(this::entityToDTO).toList();
    }
}
