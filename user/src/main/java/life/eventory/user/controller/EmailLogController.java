package life.eventory.user.controller;

import life.eventory.user.controller.api.EmailLogAPI;
import life.eventory.user.entity.EmailLogEntity;
import life.eventory.user.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmailLogController implements EmailLogAPI {
    private final EmailLogService emailLogService;

    @Override
    public ResponseEntity<List<EmailLogEntity>> getLogsByEmail(@RequestParam String email) {
        List<EmailLogEntity> logs = emailLogService.getLogsByEmail(email);
        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(logs);
    }
}
