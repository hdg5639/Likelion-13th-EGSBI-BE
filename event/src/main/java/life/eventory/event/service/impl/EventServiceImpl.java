package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.*;
import life.eventory.event.dto.activity.BookmarkResponse;
import life.eventory.event.dto.activity.HistoryResponse;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.Tag;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.repository.EventSearchRepository;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.EventService;
import life.eventory.event.service.EventTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventSearchRepository eventSearchRepository;
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
        // 주최자 유효 검사
        communicationService.existUser(newEventDTO.getOrganizerId());

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
    public List<EventDTO> findAllByOrganizerId(Pageable pageable, Long organizerId) {
        return getByOrganizer(pageable, organizerId);
    }

    @Override
    public EventDTO updateEvent(EventUpdate eventUpdate, MultipartFile image) throws IOException {
        Event event = eventRepository.findById(eventUpdate.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "행사를 찾을 수 없음"));

        Long oldImageId = event.getPosterId();
        // 혹시 모를 posterId 변조값 방지
        Long newImageId = null;

        // 이미지 파일이 있을 경우 업로드
        if (hasFile(image)) {
            newImageId = communicationService.uploadPoster(image);
        }

        // 이미지 ID 설정
        if (newImageId != null) {
            event.setPosterId(newImageId);
            if (oldImageId != null)
                communicationService.deletePoster(oldImageId);
        }

        try {
            // 포스터 존재 플래그 판별
            if (!eventUpdate.getPoster()) {
                event.setPosterId(null);
            }
            return entityToDTO(
                    eventTagService.setEventHashtags(
                            eventRepository.save(eventUpdate(eventUpdate, event)).getId(),
                            eventUpdate.getHashtags()
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

    // 기본 최신순 전체 조회
    @Override
    public List<EventDTO> getEventPage(Pageable pageable, Boolean deadline) {
        Page<Event> eventPage =
                deadline ? eventSearchRepository.findAllByPage(pageable) :
                        eventSearchRepository.findAllByPageExcludeClosed(pageable);
        return eventPage.getContent().stream()
                .map(this::entityToDTO)
                .toList();
    }

    // 거리순 전체 조회
    @Override
    public List<EventDTO> getEventPage(Pageable pageable, LocationDTO locationDTO, Boolean deadline) {
        Page<Event> eventPage =
                deadline ? eventSearchRepository.findByDistance(locationDTO, pageable) :
                        eventSearchRepository.findByDistanceExcludeClosed(locationDTO, pageable);
        return eventPage.getContent().stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public Boolean existEvent(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public EventDTO getEventById(Long eventId) {

        return entityToDTO(eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "행사를 찾을 수 없음")));
    }

    @Override
    public Boolean existOrganizer(Long organizerId) {
        return eventRepository.existsByOrganizerId(organizerId);
    }

    @Override
    public List<EventDTO> getBookmarkList(Long userId) {
        List<BookmarkResponse> bookmarks = communicationService.getBookmark(userId);
        return eventRepository.findAllById(bookmarks.stream().map(BookmarkResponse::getEventId).toList())
                .stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<EventDTO> getHistoryList(Long userId, Pageable pageable) {
        List<HistoryResponse> bookmarks = communicationService.getHistoryPage(userId, pageable);
        return eventRepository.findAllById(bookmarks.stream().map(HistoryResponse::getEventId).toList())
                .stream()
                .map(this::entityToDTO)
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
                .qrImage(event.getQrImage())
                .hashtags(
                        event.getTags() == null ?
                                List.of() :
                                event.getTags().stream()
                                        .map(Tag::getDisplayName)
                                        .toList()
                )
                .build();
    }

    private Event eventUpdate(EventUpdate eventDTO, Event event) {
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

    private List<EventDTO> getByOrganizer(Pageable pageable, Long organizerId) {
        Page<Event> events = eventRepository.findAllByOrganizerIdOrderByCreateTimeDesc(organizerId, pageable);
        return events.stream().map(this::entityToDTO).toList();
    }
}
