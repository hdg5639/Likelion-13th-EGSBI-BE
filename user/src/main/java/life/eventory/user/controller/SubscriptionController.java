package life.eventory.user.controller;

import life.eventory.user.controller.api.SubscriptionAPI;
import life.eventory.user.dto.SubscriptionCreateRequest;
import life.eventory.user.dto.SubscriptionListResponse;
import life.eventory.user.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController implements SubscriptionAPI{
    private final SubscriptionService subscriptionService;

    @Override
    public ResponseEntity<SubscriptionCreateRequest> createSubscription(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody SubscriptionCreateRequest request) {
        request.setUserId(userId);

        SubscriptionCreateRequest subscriptionCreateRequest = subscriptionService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionCreateRequest);
    }

    @Override
    public ResponseEntity<List<SubscriptionListResponse>> getAllSubscriptions(@RequestHeader("X-User-Id") Long userId) {
        List<SubscriptionListResponse> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subscriptions);
    }

    @Override
    public ResponseEntity<String> deleteSubscription(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestBody SubscriptionCreateRequest request) {
        request.setUserId(userId);
        subscriptionService.deleteSubscription(request);
        return ResponseEntity.ok("삭제 성공");
    }
}
