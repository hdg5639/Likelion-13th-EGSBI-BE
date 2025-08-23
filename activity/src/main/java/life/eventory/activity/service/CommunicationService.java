package life.eventory.activity.service;

import life.eventory.activity.dto.event.EventResponse;

import java.util.List;

public interface CommunicationService {
    List<EventResponse> getUserEvents(Long userId);
}
