package life.eventory.ai.service.impl;

import life.eventory.ai.dto.Recommender;
import life.eventory.ai.dto.activity.BookmarkResponse;
import life.eventory.ai.dto.activity.HistoryResponse;
import life.eventory.ai.dto.activity.ParticipationResponse;
import life.eventory.ai.service.AiEventRecommender;
import life.eventory.ai.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiEventRecommenderImpl implements AiEventRecommender {
    private final CommunicationService communicationService;

    @Override
    public Recommender combineInfo(Long userId) {
        Set<Long> combineInfo = new HashSet<>();

        // 기록 조회
        combineInfo.addAll(
                communicationService.getHistory(userId).stream()
                        .map(HistoryResponse::getEventId)
                        .toList()
        );

        // 북마크 조회
        combineInfo.addAll(
                communicationService.getBookmark(userId).stream()
                        .map(BookmarkResponse::getEventId)
                        .toList()
        );

        Set<Long> participationEvents = communicationService.getParticipation(userId).stream()
                .map(ParticipationResponse::getEventId)
                .collect(Collectors.toSet());
        // 참여 조회
        combineInfo.addAll(participationEvents);

        return new Recommender(communicationService.getHashtags(combineInfo), participationEvents);
    }
}
