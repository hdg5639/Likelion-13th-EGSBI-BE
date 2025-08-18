package life.eventory.user.service;

import life.eventory.user.dto.SubscriptionCreateRequest;
import life.eventory.user.entity.SubscriptionEntity;

import java.util.List;

public interface SubscriptionService{
    SubscriptionCreateRequest save(SubscriptionCreateRequest request);
    List<SubscriptionEntity> getSubscriptionsByUserId(Long userId);
    void deleteSubscription(SubscriptionCreateRequest request);
}
