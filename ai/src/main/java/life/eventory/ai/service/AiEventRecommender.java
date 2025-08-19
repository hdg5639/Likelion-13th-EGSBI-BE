package life.eventory.ai.service;

import life.eventory.ai.dto.Recommender;

public interface AiEventRecommender {
    Recommender combineInfo(Long userId);
}
