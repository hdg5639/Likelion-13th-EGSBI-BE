package life.eventory.activity.service;

import life.eventory.activity.dto.ParticipationDTO;

import java.util.List;

public interface ParticipationService {
    ParticipationDTO joinEvent(Long userId, Long eventId);
    List<ParticipationDTO> getParticipation(Long userId);
}
