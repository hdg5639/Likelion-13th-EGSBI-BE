package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.search.EventSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface EventSearchService {
    Page<EventDTO> searchFulltext(String rawQuery, Pageable pageable,
                                  @Nullable EventSearchCond extra);
}
