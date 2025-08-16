package life.eventory.activity.controller;

import life.eventory.activity.controller.api.NotificationAPI;
import life.eventory.activity.dto.NotificationDTO;
import life.eventory.activity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor

public class NotificationController implements NotificationAPI {
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<String> toggleNotification(@RequestBody NotificationDTO notificationDTO) {
        String message = notificationService.toggleNotification(
                notificationDTO.getUserId(), notificationDTO.getEventId());
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<List<NotificationDTO>> notificationList(@RequestParam Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationList(userId);
        return ResponseEntity.ok(notifications);
    }
}
