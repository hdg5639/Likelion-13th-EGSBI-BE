package life.eventory.activity.controller;

import life.eventory.activity.controller.api.ParticipationAPI;
import life.eventory.activity.dto.ParticipationDTO;
import life.eventory.activity.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ParticipationController implements ParticipationAPI {
    private final ParticipationService participationService;

    @Override
    public ResponseEntity<ParticipationDTO> joinEvent(@RequestBody ParticipationDTO participationDTO) {
        ParticipationDTO participation = participationService.joinEvent(
                participationDTO.getUserId(),
                participationDTO.getEventId());
        return ResponseEntity.ok(participation);
    }

    @Override
    public ResponseEntity<List<ParticipationDTO>> getParticipations(@RequestParam Long userId) {
        List<ParticipationDTO> participation = participationService.getParticipation(userId);
        return ResponseEntity.ok(participation);
    }


}
