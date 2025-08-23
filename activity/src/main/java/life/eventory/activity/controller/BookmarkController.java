package life.eventory.activity.controller;

import life.eventory.activity.controller.api.BookmarkAPI;
import life.eventory.activity.dto.bookmark.BookmarkRequestDTO;
import life.eventory.activity.dto.bookmark.BookmarkResponseDTO;
import life.eventory.activity.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController implements BookmarkAPI {
    private final BookmarkService bookmarkService;

    @Override
    public ResponseEntity<String> toggleBookmark(@RequestHeader("X-User-Id") Long userId,
                                                 @RequestBody BookmarkRequestDTO requestDTO) {
        String message = bookmarkService.toggleBookmark(
                userId, requestDTO.getEventId());
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<List<BookmarkResponseDTO>> listBookmark(@RequestHeader("X-User-Id") Long userId) {
        List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmarkedEvents(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @Override
    public ResponseEntity<Long> getBookmarkCount(@RequestParam Long eventId) {
        Long count = bookmarkService.getBookmarkCountByEvent(eventId);
        return ResponseEntity.ok(count);
    }

    @Override
    public ResponseEntity<LinkedHashMap<Long, Long>> getBookmarkedEvents() {
        return ResponseEntity.ok(bookmarkService.getBookmarkedEvents());
    }

}
