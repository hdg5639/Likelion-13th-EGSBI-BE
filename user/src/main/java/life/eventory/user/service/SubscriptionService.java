package life.eventory.user.service;

import life.eventory.user.dto.SubscriptionCreateRequest;
import life.eventory.user.dto.SubscriptionListResponse;

import java.util.List;

public interface SubscriptionService{
    SubscriptionCreateRequest save(SubscriptionCreateRequest request);
    List<SubscriptionListResponse> getSubscriptionsByUserId(Long userId);
    void deleteSubscription(SubscriptionCreateRequest request);
    List<Long> getUser(Long organizerId);
}
