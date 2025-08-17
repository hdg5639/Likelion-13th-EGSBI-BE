package life.eventory.activity.controller;

import life.eventory.activity.controller.api.BookmarkAPI;
import life.eventory.activity.dto.BookmarkDTO;
import life.eventory.activity.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController implements BookmarkAPI {
    private final BookmarkService bookmarkService;

    @Override
    public ResponseEntity<String> toggleBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        String message = bookmarkService.toggleBookmark(
                bookmarkDTO.getUserId(), bookmarkDTO.getEventId());
        return ResponseEntity.ok(message);
    }

    @Override
    public ResponseEntity<List<BookmarkDTO>> listBookmark(@RequestParam Long userId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarkedEvents(userId);
        return ResponseEntity.ok(bookmarks);
    }

}
