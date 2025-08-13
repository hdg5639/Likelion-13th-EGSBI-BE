package life.eventory.event.service;

import life.eventory.event.entity.Event;

import java.util.List;

public interface EventTagService {
    Event setEventHashtags(Long eventId, List<String> rawTags);
}
