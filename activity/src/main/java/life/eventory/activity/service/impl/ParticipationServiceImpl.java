package life.eventory.activity.service.impl;

import life.eventory.activity.dto.ParticipationDTO;
import life.eventory.activity.entity.ParticipationEntity;
import life.eventory.activity.repository.ParticipationRepository;
import life.eventory.activity.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;

    public ParticipationDTO joinEvent(Long userId, Long eventId) {
        ParticipationEntity participation = ParticipationEntity.builder()
                .userId(userId)
                .eventId(eventId)
                .joinedAt(LocalDateTime.now())
                .build();
        return participationRepository.save(participation).toDTO();
    }

    public List<ParticipationDTO> getParticipation(Long userId) {
        return participationRepository.findByUserId(userId).stream().map(ParticipationEntity::toDTO).toList();
    }
}
