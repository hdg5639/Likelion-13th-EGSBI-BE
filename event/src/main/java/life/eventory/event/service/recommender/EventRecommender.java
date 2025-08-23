package life.eventory.event.service.recommender;

import life.eventory.event.dto.recommender.AiRecommendResponse;


public interface EventRecommender {
    AiRecommendResponse recommendWithComment(Long userId);
}
