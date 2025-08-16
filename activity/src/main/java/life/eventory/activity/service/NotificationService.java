package life.eventory.activity.service;

import life.eventory.activity.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    String toggleNotification(Long userId, Long eventId);
    List<NotificationDTO> getNotificationList(Long userId);
}
