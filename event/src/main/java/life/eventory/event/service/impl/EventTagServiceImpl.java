package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.Tag;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.repository.TagRepository;
import life.eventory.event.service.EventTagService;
import life.eventory.event.service.TagNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventTagServiceImpl implements EventTagService {
    private final TagRepository tagRepository;
    private final EventRepository eventRepository;
    private final TagNormalizer normalizer;

    @Override
    @Transactional
    public Event setEventHashtags(Long eventId, List<String> rawTags) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<String> normalized = rawTags == null ? List.of() :
                rawTags.stream()
                        .map(normalizer::normalize)
                        .filter(Objects::nonNull)
                        .distinct()
                        .limit(10) // 이벤트당 최대 10개 등 정책
                        .toList();

        // 기존 태그 조회
        List<Tag> existing = normalized.isEmpty()
                ? List.of()
                : tagRepository.findAllByNameIn(normalized);
        Map<String, Tag> byName = existing.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        // 없는 태그 생성
        List<Tag> toCreate = normalized.stream()
                .filter(n -> !byName.containsKey(n))
                .map(n -> Tag.builder().name(n).displayName(n).build())
                .toList();
        if (!toCreate.isEmpty()) {
            tagRepository.saveAll(toCreate);
            toCreate.forEach(t -> byName.put(t.getName(), t));
        }

        // 이벤트에 세팅(교체)
        event.getTags().clear();
        event.getTags().addAll(normalized.stream().map(byName::get).toList());

        return eventRepository.save(event);
    }
}
