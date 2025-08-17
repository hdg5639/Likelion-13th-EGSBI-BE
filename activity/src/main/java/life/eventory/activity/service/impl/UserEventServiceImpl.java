package life.eventory.activity.service.impl;

import life.eventory.activity.dto.UserEventDTO;
import life.eventory.activity.entity.UserEventEntity;
import life.eventory.activity.repository.UserEventRepository;
import life.eventory.activity.service.UserEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEventServiceImpl implements UserEventService {
    private final UserEventRepository userEventRepository;

    @Override
    public List<UserEventDTO> getUserEvents(Long userId) {
        return userEventRepository.findByUserId(userId).stream().map(UserEventEntity::toDTO).toList();
    }

    @Override
    public Map<Long,List<UserEventDTO>> getEventByUserId(List<Long> userId) {
        return userEventRepository.findByUserIdIn(userId).stream().map(UserEventEntity::toDTO).distinct()
                .collect(Collectors.groupingBy(UserEventDTO::getUserId));
    }

}
