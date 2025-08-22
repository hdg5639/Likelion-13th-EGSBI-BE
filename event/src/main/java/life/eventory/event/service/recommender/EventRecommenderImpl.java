package life.eventory.event.service.recommender;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.activity.BookmarkResponse;
import life.eventory.event.dto.activity.HistoryResponse;
import life.eventory.event.dto.activity.ParticipationResponse;
import life.eventory.event.dto.recommender.AiRecommendResponse;
import life.eventory.event.dto.recommender.Recommender;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.Tag;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRecommenderImpl implements EventRecommender {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final CommunicationService communicationService;

    private Recommender combineInfo(Long userId) {
        var history = communicationService.getHistory(userId);
        var bookmarks = communicationService.getBookmark(userId);
        var participations = communicationService.getParticipation(userId);

        Set<Long> excludeEventIds = new HashSet<>();
        excludeEventIds.addAll(history.stream().map(HistoryResponse::getEventId).toList());
        excludeEventIds.addAll(bookmarks.stream().map(BookmarkResponse::getEventId).toList());
        excludeEventIds.addAll(participations.stream().map(ParticipationResponse::getEventId).toList());

        List<Long> allEventIds = excludeEventIds.stream().toList();
        Set<Long> idSet = new HashSet<>(allEventIds);
        List<Event> events = eventRepository.findAllWithTagsByIdIn(idSet);

        Map<Long, Event> eventMap = events.stream().collect(Collectors.toMap(Event::getId, e -> e));

        final double VIEW_W  = 1.0;
        final double BOOK_W  = 3.0;
        final double PART_W  = 5.0;
        final double LAMBDA  = Math.log(2) / 30.0;
        final LocalDateTime now = LocalDateTime.now();

        Map<Long, Double> tagWeights = new HashMap<>();

        BiConsumer<Long, Double> addWeightByEvent = (eventId, base) -> {
            Event e = eventMap.get(eventId);
            if (e == null) return;
            int tagCount = Math.max(1, e.getTags().size());
            double perTag = base / Math.sqrt(tagCount);
            for (Tag tag : e.getTags()) {
                tagWeights.merge(tag.getId(), perTag, Double::sum);
            }
        };

        Function<LocalDateTime, Double> decay = ts -> {
            if (ts == null) return 1.0;
            long days = Math.max(0, ChronoUnit.DAYS.between(ts.toLocalDate().atStartOfDay(), now.toLocalDate().atStartOfDay()));
            return Math.exp(-LAMBDA * days);
        };

        for (var h : history) {
            double w = VIEW_W * decay.apply(getViewedAt(h));
            addWeightByEvent.accept(h.getEventId(), w);
        }

        for (var b : bookmarks) {
            double w = BOOK_W * decay.apply(getBookmarkedAt(b));
            addWeightByEvent.accept(b.getEventId(), w);
        }

        for (var p : participations) {
            double w = PART_W * decay.apply(getParticipatedAt(p));
            addWeightByEvent.accept(p.getEventId(), w);
        }

        List<Long> topTagIds = tagWeights.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        return Recommender.builder()
                .tagWeights(tagWeights)
                .excludeEventIds(excludeEventIds)
                .topTagIds(topTagIds)
                .build();
    }

    private LocalDateTime getViewedAt(HistoryResponse r) {
        try { return r.getViewedAt(); } catch (Exception e) { return null; }
    }
    private LocalDateTime getBookmarkedAt(BookmarkResponse r) {
        try { return r.getCreatedAt(); } catch (Exception e) { return null; }
    }
    private LocalDateTime getParticipatedAt(ParticipationResponse r) {
        try { return r.getJoinedAt(); } catch (Exception e) { return null; }
    }

    // 메인 로직
    @Override
    public AiRecommendResponse recommendWithComment(Long userId) {
        Recommender rec = combineInfo(userId);
        Map<Long, Double> tagWeights = rec.getTagWeights();
        Set<Long> topTagIds = new LinkedHashSet<>(rec.getTopTagIds());
        Set<Long> excludeIds = rec.getExcludeEventIds();

        if (topTagIds.isEmpty() || tagWeights.isEmpty()) {
            return new AiRecommendResponse("아직 추천 태그가 없어서, 대신 방금 올라온 최신 행사들을 가져왔어요!",
                    eventService.getEventPage(PageRequest.of(0, 20), Boolean.FALSE));
        }

        final LocalDateTime now = LocalDateTime.now();

        List<Long> candidateIds = eventRepository.findCandidateIds(
                topTagIds,
                (excludeIds == null || excludeIds.isEmpty()) ? null : excludeIds,
                now,
                PageRequest.of(0, 200)
        );
        if (candidateIds.isEmpty()) {
            return new AiRecommendResponse("아직 추천 행사를 생성할 수 없습니다. 최신 행사 목록을 확인해보세요!",
                    eventService.getEventPage(PageRequest.of(0, 20), Boolean.FALSE));
        }

        List<Event> candidates = eventRepository.findAllWithTagsByIdInOrderAgnostic(candidateIds);

        List<EventDTO> top10 = candidates.stream()
                .map(e -> Map.entry(e, computeScore(e, tagWeights, now)))
                .sorted(
                        Map.Entry.<Event, Double>comparingByValue()
                                .reversed()
                                .thenComparing(entry -> entry.getKey().getStartTime())
                )
                .limit(10)
                .map(entry -> toEventDTO(entry.getKey()))
                .toList();

        List<String> keywords = collectTopTagDisplayNamesInOrder(candidates, tagWeights, topTagIds);

        String prompt = buildCommentPrompt(keywords, top10);
        String comment;
        try {
            comment = communicationService.getComment(prompt);
            if (comment == null || comment.isBlank()) comment = defaultComment(keywords);
        } catch (Exception e) {
            comment = defaultComment(keywords);
        }

        return new AiRecommendResponse(comment, top10);
    }

    private List<String> collectTopTagDisplayNamesInOrder(List<Event> candidates,
                                                          Map<Long, Double> tagWeights,
                                                          Set<Long> topTagIds) {
        List<Long> orderedTopTagIds = tagWeights.entrySet().stream()
                .filter(e -> topTagIds.contains(e.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        Map<Long, String> idToDisplay = new LinkedHashMap<>();
        for (Event e : candidates) {
            if (e.getTags() == null) continue;
            for (Tag t : e.getTags()) {
                if (!idToDisplay.containsKey(t.getId())) {
                    idToDisplay.put(t.getId(), t.getDisplayName() != null ? t.getDisplayName() : t.getName());
                }
            }
        }

        return orderedTopTagIds.stream()
                .map(idToDisplay::get)
                .filter(Objects::nonNull)
                .distinct()
                .limit(10)
                .toList();
    }

    private String buildCommentPrompt(List<String> keywords, List<EventDTO> events) {
        String tags = String.join(", ", keywords);
        StringBuilder sb = new StringBuilder();
        sb.append("사용자 관심 태그: ").append(tags).append("\n");
        sb.append("아래 행사 리스트를 보고 사용자가 좋아할 만한 이유를 한국어로 1문장(최대 28자)으로 요약해줘. ");
        sb.append("부드럽고 설레는 톤, 너무 과장 금지.\n");
        sb.append("행사들:\n");
        for (EventDTO e : events) {
            sb.append("- ").append(e.getName());
            if (e.getStartTime() != null) sb.append(" @ ").append(e.getStartTime());
            if (e.getEntryFee() != null) sb.append(" / fee=").append(e.getEntryFee());
            if (e.getHashtags() != null && !e.getHashtags().isEmpty()) {
                sb.append(" / tags=").append(String.join("/", e.getHashtags()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String defaultComment(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return "취향에 맞는 행사를 골라봤어요.";
        String top = String.join(", ", keywords.stream().limit(3).toList());
        return top + " 취향에 꼭 맞는 행사들이에요.";
    }

    private double computeScore(Event e, Map<Long, Double> tagWeights, LocalDateTime now) {
        double tagScore = e.getTags().stream()
                .mapToDouble(t -> tagWeights.getOrDefault(t.getId(), 0.0))
                .sum();

        long daysUntilStart = Math.max(0, Duration.between(now, e.getStartTime()).toDays());
        double recencyBonus = Math.max(0, 30 - daysUntilStart) * 0.05; // 0~1.5

        double priceBonus = 0.0;
        Integer fee = e.getEntryFee();
        if (fee != null) {
            if (fee == 0) priceBonus = 0.5;
            else if (fee <= 10_000) priceBonus = 0.2;
        }

        return tagScore + recencyBonus + priceBonus;
    }

    private EventDTO toEventDTO(Event e) {
        List<String> tagNames = e.getTags() == null ? List.of()
                : e.getTags().stream()
                .map(Tag::getName)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return EventDTO.builder()
                .id(e.getId())
                .organizerId(e.getOrganizerId())
                .name(e.getName())
                .posterId(e.getPosterId())
                .description(e.getDescription())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .address(e.getAddress())
                .latitude(e.getLatitude())
                .longitude(e.getLongitude())
                .entryFee(e.getEntryFee())
                .createTime(e.getCreateTime())
                .qrImage(e.getQrImage())
                .hashtags(tagNames)
                .build();
    }

}
