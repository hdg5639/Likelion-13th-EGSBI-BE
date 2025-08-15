package life.eventory.activity.service;

import life.eventory.activity.dto.BookmarkDTO;

import java.util.List;

public interface BookmarkService {
    String toggleBookmark(Long userId, Long eventId);
    List<BookmarkDTO> getBookmarkedEvents(Long userId);
}
