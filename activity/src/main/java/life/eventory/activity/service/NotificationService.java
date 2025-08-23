package life.eventory.activity.service;

import life.eventory.activity.dto.notification.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    String toggleNotification(Long userId, Long eventId);
    List<NotificationResponseDTO> getNotificationList(Long userId);
}
