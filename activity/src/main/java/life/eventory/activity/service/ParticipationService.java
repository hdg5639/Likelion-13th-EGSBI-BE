package life.eventory.activity.service;

import life.eventory.activity.dto.participation.ParticipationRequestDTO;
import life.eventory.activity.dto.participation.ParticipationResponseDTO;

import java.util.List;

public interface ParticipationService {
    ParticipationResponseDTO joinEvent(Long userId, ParticipationRequestDTO requestDTO);
    List<ParticipationResponseDTO> getParticipation(Long userId);
}
