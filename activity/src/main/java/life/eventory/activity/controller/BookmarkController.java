package life.eventory.activity.controller;

import life.eventory.activity.dto.BookmarkDTO;
import life.eventory.activity.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleBookmark(@RequestBody BookmarkDTO bookmarkDTO) {
        Long userId = bookmarkDTO.getUserId();
        Long eventId = bookmarkDTO.getEventId();

        boolean isBookmarked = bookmarkService.toggleBookmark(userId, eventId);

        if (isBookmarked) {
            return ResponseEntity.ok("북마크가 설정되었습니다.");
        }
        else {
            return ResponseEntity.ok("북마크가 해제되었습니다.");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<BookmarkDTO>> listBookmark(@RequestParam Long userId) {
        List<BookmarkDTO> bookmarks = bookmarkService.getBookmarkedEvents(userId);
        return ResponseEntity.ok(bookmarks);
    }
}
