package life.eventory.activity.service.impl;

import life.eventory.activity.dto.BookmarkDTO;
import life.eventory.activity.dto.NotificationDTO;
import life.eventory.activity.entity.BookmarkEntity;
import life.eventory.activity.entity.NotificationEntity;
import life.eventory.activity.repository.NotificationRepository;
import life.eventory.activity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
//    private final UserRepository userRepository;
//    private final EventRepository eventRepository;

    @Override
    public String toggleNotification(Long userId, Long eventId) {
//        //사용자 검증
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new RuntimeException("사용자 없음"));
//
//        // 행사 존재 유무 검증
//        EventEntity event = eventRepository.findById(eventId)
//                .orElseThrow(()-> new RuntimeException("행사 없음"));

        // 기존 알림 확인
        Optional<NotificationEntity> notification = notificationRepository.findByUserIdAndEventId(userId, eventId);

        // 알림이 존재할 때 삭제(알림 해제)
        if ( notification.isPresent()) {
            notificationRepository.delete(notification.get());
            return "알림이 해제되었습니다.";
        }
        else { // 알림이 없어서 새로 추가(알림 설정)
            notificationRepository.save(NotificationEntity.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .createdAt(LocalDateTime.now())
                    .build());
            return "알림이 설정되었습니다.";
        }
    }

    // 사용자가 알림받기 한 행사 리스트 조회
    @Override
    public List<NotificationDTO> getNotificationList(Long userId) {
        return notificationRepository.findByUserId(userId).stream().map(NotificationEntity::toDTO).toList();
    }
}
