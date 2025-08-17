package life.eventory.activity.service;

import life.eventory.activity.dto.UserEventDTO;

import java.util.List;
import java.util.Map;

public interface UserEventService {
    List<UserEventDTO> getUserEvents(Long userId);
    Map<Long,List<UserEventDTO>> getEventByUserId(List<Long> userId);
    }
