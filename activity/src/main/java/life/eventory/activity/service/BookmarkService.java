package life.eventory.activity.service;

import life.eventory.activity.dto.bookmark.BookmarkResponseDTO;

import java.util.List;

public interface BookmarkService {
    String toggleBookmark(Long userId, Long eventId);
    List<BookmarkResponseDTO> getBookmarkedEvents(Long userId);
    Long getBookmarkCountByEvent(Long eventId);
    List<BookmarkResponseDTO> getBookmarkedEvents();
}
