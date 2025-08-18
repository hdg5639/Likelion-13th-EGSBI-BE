package life.eventory.activity.controller;

import life.eventory.activity.controller.api.NotificationAPI;
import life.eventory.activity.dto.notification.NotificationRequestDTO;
import life.eventory.activity.dto.notification.NotificationResponseDTO;
import life.eventory.activity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor

public class NotificationController implements NotificationAPI {
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<String> toggleNotification(@RequestHeader("X-User-Id") Long userId,
                                                     @RequestBody NotificationRequestDTO requestDTO) {
        String message = notificationService.toggleNotification(
                userId, requestDTO.getEventId());
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<List<NotificationResponseDTO>> notificationList(@RequestHeader("X-User-Id") Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getNotificationList(userId);
        return ResponseEntity.ok(notifications);
    }
}
