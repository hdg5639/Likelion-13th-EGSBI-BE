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
    private final EventRepository eventRepository;
    private final CommunicationService communicationService;

    private Recommender combineInfo(Long userId) {
        // 1) 신호 수집: 이벤트 ID + (가능하면 타임스탬프)
        var history = communicationService.getHistory(userId);         // List<HistoryResponse>
        var bookmarks = communicationService.getBookmark(userId);      // List<BookmarkResponse>
        var participations = communicationService.getParticipation(userId); // List<ParticipationResponse>

        // 2) 제외 이벤트 집합(참여 + 북마크 + 과거 본 것들까지 모두)
        Set<Long> excludeEventIds = new HashSet<>();
        excludeEventIds.addAll(history.stream().map(HistoryResponse::getEventId).toList());
        excludeEventIds.addAll(bookmarks.stream().map(BookmarkResponse::getEventId).toList());
        excludeEventIds.addAll(participations.stream().map(ParticipationResponse::getEventId).toList());

        // 3) 이벤트 → 태그 로딩
        List<Long> allEventIds = excludeEventIds.stream().toList();
        Set<Long> idSet = new HashSet<>(allEventIds);
        List<Event> events = eventRepository.findAllWithTagsByIdIn(idSet);

        // id -> Event 매핑
        Map<Long, Event> eventMap = events.stream().collect(Collectors.toMap(Event::getId, e -> e));

        // 4) 가중치 설정
        final double VIEW_W  = 1.0;
        final double BOOK_W  = 3.0;
        final double PART_W  = 5.0;
        final double LAMBDA  = Math.log(2) / 30.0; // 30일 반감기 (원하면 조정)
        final LocalDateTime now = LocalDateTime.now();

        // 5) 태그 가중치 누적
        Map<Long, Double> tagWeights = new HashMap<>();

        // 공통 로직: 한 이벤트의 각 태그에 가중치 분배
        BiConsumer<Long, Double> addWeightByEvent = (eventId, base) -> {
            Event e = eventMap.get(eventId);
            if (e == null) return; // 태그 없는 이벤트거나 로딩 실패
            int tagCount = Math.max(1, e.getTags().size());
            double perTag = base / Math.sqrt(tagCount);
            for (Tag tag : e.getTags()) {
                tagWeights.merge(tag.getId(), perTag, Double::sum);
            }
        };

        // 시간 감쇠 계산 헬퍼 (타임스탬프 없으면 1.0)
        Function<LocalDateTime, Double> decay = ts -> {
            if (ts == null) return 1.0;
            long days = Math.max(0, ChronoUnit.DAYS.between(ts.toLocalDate().atStartOfDay(), now.toLocalDate().atStartOfDay()));
            return Math.exp(-LAMBDA * days);
        };

        // 5-1) 조회 신호
        for (var h : history) {
            double w = VIEW_W * decay.apply(getViewedAt(h)); // getViewedAt는 아래 주석 참고
            addWeightByEvent.accept(h.getEventId(), w);
        }

        // 5-2) 북마크 신호
        for (var b : bookmarks) {
            double w = BOOK_W * decay.apply(getBookmarkedAt(b));
            addWeightByEvent.accept(b.getEventId(), w);
        }

        // 5-3) 참여 신호
        for (var p : participations) {
            double w = PART_W * decay.apply(getParticipatedAt(p));
            addWeightByEvent.accept(p.getEventId(), w);
        }

        // 6) 상위 태그 10개 뽑기 (필요 시 개수 조정)
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
    // 추천 + GPT 코멘트 동시 반환
    @Override
    public AiRecommendResponse recommendWithComment(Long userId) {
        Recommender rec = combineInfo(userId);
        Map<Long, Double> tagWeights = rec.getTagWeights();
        Set<Long> topTagIds = new LinkedHashSet<>(rec.getTopTagIds());
        Set<Long> excludeIds = rec.getExcludeEventIds();

        if (topTagIds.isEmpty() || tagWeights.isEmpty()) {
            return new AiRecommendResponse("최근 활동을 바탕으로 추천할 태그가 아직 없어요.",
                    Collections.emptyList());
        }

        final LocalDateTime now = LocalDateTime.now();

        // 1) 후보 ID만 페이지로 수집 (200개 컷)
        List<Long> candidateIds = eventRepository.findCandidateIds(
                topTagIds,
                (excludeIds == null || excludeIds.isEmpty()) ? null : excludeIds,
                now,
                PageRequest.of(0, 200)
        );
        if (candidateIds.isEmpty()) {
            return new AiRecommendResponse("조건에 맞는 행사를 찾지 못했어요.",
                    Collections.emptyList());
        }

        // 2) 태그 fetch join으로 로딩
        List<Event> candidates = eventRepository.findAllWithTagsByIdInOrderAgnostic(candidateIds);

        // 3) 점수 계산 → 정렬 → 상위 10 → DTO
        List<EventDTO> top10 = candidates.stream()
                .map(e -> Map.entry(e, computeScore(e, tagWeights, now)))
                .sorted(
                        Comparator.<Map.Entry<Event, Double>, Double>comparing(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(entry -> entry.getKey().getStartTime())
                )
                .limit(10)
                .map(entry -> toEventDTO(entry.getKey()))
                .toList();

        // 4) GPT 코멘트용 키워드(태그 displayName) 뽑기
        List<String> keywords = collectTopTagDisplayNamesInOrder(candidates, tagWeights, topTagIds);

        // 5) 프롬프트 구성 → GPT 코멘트 요청
        String prompt = buildCommentPrompt(keywords, top10);
        String comment;
        try {
            comment = communicationService.getComment(prompt); // 통신 실패 대비
            if (comment == null || comment.isBlank()) comment = defaultComment(keywords);
        } catch (Exception e) {
            comment = defaultComment(keywords);
        }

        return new AiRecommendResponse(comment, top10);
    }

    // 태그 가중치 내림차순 순서대로 displayName을 최대 10개 뽑기
    private List<String> collectTopTagDisplayNamesInOrder(List<Event> candidates,
                                                          Map<Long, Double> tagWeights,
                                                          Set<Long> topTagIds) {
        // 태그 id를 가중치 순으로 정렬
        List<Long> orderedTopTagIds = tagWeights.entrySet().stream()
                .filter(e -> topTagIds.contains(e.getKey()))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        // 후보에서 해당 태그들의 displayName 매핑 구성
        Map<Long, String> idToDisplay = new LinkedHashMap<>();
        for (Event e : candidates) {
            if (e.getTags() == null) continue;
            for (Tag t : e.getTags()) {
                if (!idToDisplay.containsKey(t.getId())) {
                    idToDisplay.put(t.getId(), t.getDisplayName() != null ? t.getDisplayName() : t.getName());
                }
            }
        }

        // 정렬된 top-id 순으로 displayName 리스트 생성
        return orderedTopTagIds.stream()
                .map(idToDisplay::get)
                .filter(Objects::nonNull)
                .distinct()
                .limit(10)
                .toList();
    }

    private String buildCommentPrompt(List<String> keywords, List<EventDTO> events) {
        // 입력 크기/비용을 줄이기 위해 간결하게 전달
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
