package life.eventory.activity.controller;

import life.eventory.activity.controller.api.ReviewAPI;
import life.eventory.activity.dto.review.DetailReview;
import life.eventory.activity.dto.review.ReviewRequestDTO;
import life.eventory.activity.dto.review.ReviewResponseDTO;
import life.eventory.activity.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewAPI {
    private final ReviewService reviewService;

    @Override
    public ResponseEntity<?> createReview(@RequestHeader("X-User-Id") Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody ReviewRequestDTO requestDTO) {
        try {
            ReviewResponseDTO responseDTO = reviewService.createReview(userId, eventId, requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByEvent(Long eventId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByEvent(eventId);
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<Double> getAvgRatingByUser(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                     @RequestParam(required = false) Long targetId) {
        if (targetId == null)
            return ResponseEntity.ok(reviewService.getAvgRatingByUser(userId));

        return ResponseEntity.ok(reviewService.getAvgRatingByUser(targetId));
    }

    @Override
    public ResponseEntity<List<String>> getUserReviews (
            @RequestHeader(name = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) Long targetId) {

        if (targetId == null)
            return ResponseEntity.ok(reviewService.getUserReviews(userId));

        return ResponseEntity.ok(reviewService.getUserReviews(targetId));
    }

    @Override
    public ResponseEntity<List<DetailReview>> getUserDetailReviews (
            @RequestHeader(name = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) Long targetId) {

        if (targetId == null)
            return ResponseEntity.ok(reviewService.getUserDetailReviews(userId));

        return ResponseEntity.ok(reviewService.getUserDetailReviews(targetId));
    }

}
