package life.eventory.activity.controller;

import life.eventory.activity.controller.api.ParticipationAPI;
import life.eventory.activity.dto.participation.ParticipationRequestDTO;
import life.eventory.activity.dto.participation.ParticipationResponseDTO;
import life.eventory.activity.service.BookmarkService;
import life.eventory.activity.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParticipationController implements ParticipationAPI {
    private final ParticipationService participationService;
    private final BookmarkService bookmarkService;

    @Override
    public ResponseEntity<ParticipationResponseDTO> joinEvent(@RequestHeader("X-User-Id") Long userId,
                                                              @RequestBody ParticipationRequestDTO requestDTO) {
        ParticipationResponseDTO response = participationService.joinEvent(userId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<ParticipationResponseDTO>> getParticipations(@RequestHeader("X-User-Id") Long userId) {
        List<ParticipationResponseDTO> list = participationService.getParticipation(userId);
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<Long>> getUserIdsByEvent(@PathVariable Long eventId) {
        List<Long> userIds = bookmarkService.getUserIdsByEvent(eventId);
        return ResponseEntity.ok(userIds);
    }
}