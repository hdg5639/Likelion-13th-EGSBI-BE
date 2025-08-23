package life.eventory.activity.service;

import life.eventory.activity.dto.review.ReviewRequestDTO;
import life.eventory.activity.dto.review.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(Long userId, Long eventId, ReviewRequestDTO requestDTO);
    List<ReviewResponseDTO> getReviewsByEvent(Long eventId);
    Double getAvgRatingByUser(Long userId);
    List<String> getUserReviews(Long userId);
}