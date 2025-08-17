package life.eventory.activity.controller;

import life.eventory.activity.controller.api.UserEventAPI;
import life.eventory.activity.dto.UserEventDTO;
import life.eventory.activity.service.UserEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserEventController implements UserEventAPI {
    private final UserEventService userEventService;

    // 단일 사용자 행사 조회
    @Override
    public ResponseEntity<List<UserEventDTO>> getUserEvents(@PathVariable Long userId) {
        List<UserEventDTO> events = userEventService.getUserEvents(userId);
        return ResponseEntity.ok(events);
    }

    // 여러 사용자의 행사 일괄 조회
    @Override
    public ResponseEntity<Map<Long, List<UserEventDTO>>> getAllEvents(@RequestParam List<Long> userId) {
        return ResponseEntity.ok(userEventService.getEventByUserId(userId));
    }
}
