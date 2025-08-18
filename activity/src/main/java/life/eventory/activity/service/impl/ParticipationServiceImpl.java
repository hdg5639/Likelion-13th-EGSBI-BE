package life.eventory.activity.service.impl;

import life.eventory.activity.dto.participation.ParticipationRequestDTO;
import life.eventory.activity.dto.participation.ParticipationResponseDTO;
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

    @Override
    public ParticipationResponseDTO joinEvent(Long userId, ParticipationRequestDTO requestDTO) {
        ParticipationEntity participation = participationRepository
                .findByUserIdAndEventId(userId, requestDTO.getEventId())
                        .orElseGet(()->{
                            ParticipationEntity newParticipation = ParticipationEntity.builder()
                                    .userId(userId)
                                    .eventId(requestDTO.getEventId())
                                    .joinedAt(LocalDateTime.now())
                                    .build();
                            return participationRepository.save(newParticipation);
                        });

        return new ParticipationResponseDTO(
                participation.getUserId(),
                participation.getEventId(),
                participation.getJoinedAt()
        );
    }

    @Override
    public List<ParticipationResponseDTO> getParticipation(Long userId) {
        return participationRepository.findByUserId(userId).stream()
                .map(p-> new ParticipationResponseDTO(
                        p.getUserId(),
                        p.getEventId(),
                        p.getJoinedAt()
                ))
                .toList();
    }
}
