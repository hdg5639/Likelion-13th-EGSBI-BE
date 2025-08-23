package life.eventory.activity.service.impl;

import life.eventory.activity.dto.event.EventResponse;
import life.eventory.activity.dto.review.ReviewRequestDTO;
import life.eventory.activity.dto.review.ReviewResponseDTO;
import life.eventory.activity.entity.ReviewEntity;
import life.eventory.activity.repository.ParticipationRepository;
import life.eventory.activity.repository.ReviewRepository;
import life.eventory.activity.service.CommunicationService;
import life.eventory.activity.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final CommunicationService communicationService;
    private final ReviewRepository reviewRepository;
    private final ParticipationRepository participationRepository;

    @Override
    public ReviewResponseDTO createReview(Long userId, Long eventId, ReviewRequestDTO requestDTO) {

        // 참여 여부 확인
        if (!participationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("행사에 참여한 기록이 없습니다.");
        }

        // 중복 리뷰 확인
        if (reviewRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new IllegalStateException("이미 리뷰를 작성한 행사입니다.");
        }

        ReviewEntity review = ReviewEntity.builder()
                .userId(userId)
                .eventId(eventId)
                .content(requestDTO.getContent())
                .rating(requestDTO.getRating())
                .build();

        return reviewRepository.save(review).toDTO();
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByEvent(Long eventId) {
        return reviewRepository.findAllByEventIdOrderByCreatedAtDesc(eventId).stream().map(ReviewEntity::toDTO).toList();

    }

    @Override
    public Double getAvgRatingByUser(Long userId) {
        return reviewRepository.findAvgRatingByUserId(userId);
    }

    @Override
    public List<String> getUserReviews(Long userId) {
        List<EventResponse> events = communicationService.getUserEvents(userId);
        List<Long> eventIds = events.stream()
                .filter(e -> e.getOrganizerId().equals(userId))
                .map(EventResponse::getId)
                .toList();
        return reviewRepository.findReviewsByEventIds(eventIds);
    }
}
