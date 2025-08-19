package life.eventory.user.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.user.dto.SubscriptionCreateRequest;
import life.eventory.user.entity.SubscriptionEntity;
import life.eventory.user.repository.SubscriptionRepository;
import life.eventory.user.repository.UserRepository;
import life.eventory.user.service.CommunicationService;
import life.eventory.user.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CommunicationService communicationService;

    @Override
    public SubscriptionCreateRequest save(SubscriptionCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }

        boolean exists = subscriptionRepository
                .findByUserIdAndOrganizerId(request.getUserId(), request.getOrganizerId())
                .isPresent();
        if (exists) {
            throw new IllegalStateException("이미 구독한 항목입니다.");
        }

        boolean organizerExists = communicationService.organizerExists(request.getOrganizerId());
        if (!organizerExists) {
            throw new IllegalArgumentException("해당 주최자가 존재하지 않습니다.");
        }

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .userId(request.getUserId())
                .organizerId(request.getOrganizerId())
                .build();

        SubscriptionEntity saved = subscriptionRepository.save(subscription);

        return SubscriptionCreateRequest.builder()
                .userId(saved.getUserId())
                .organizerId(saved.getOrganizerId())
                .build();
    }

    public List<SubscriptionEntity> getSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public void deleteSubscription(SubscriptionCreateRequest request) {
        Long organizerId = request.getOrganizerId();
        subscriptionRepository.deleteByOrganizerId(organizerId);
    }
}
