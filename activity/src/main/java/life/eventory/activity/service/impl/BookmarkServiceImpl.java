package life.eventory.activity.service.impl;

import life.eventory.activity.dto.bookmark.BookmarkResponseDTO;
import life.eventory.activity.entity.BookmarkEntity;
import life.eventory.activity.repository.BookmarkRepository;
import life.eventory.activity.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
//    private final UserRepository userRepository;
//    private final EventRepository eventRepository;

    @Override
    public String toggleBookmark(Long userId, Long eventId) {
//        // 사용자 검증
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(()-> new RuntimeException("사용자 없음"));
//
//        // 행사 존재 유무 검증
//        EventEntity event = eventRepository.findById(eventId)
//                .orElseThrow(()-> new RuntimeException("행사 없음"));

        // 기존 북마크 확인
        Optional<BookmarkEntity> bookmark = bookmarkRepository.findByUserIdAndEventId(userId, eventId);

        // 북마크가 존재할 때 삭제(북마크 해제)
        if ( bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());

            return "북마크가 해제되었습니다.";
        }
        else { // 북마크가 없어서 새로 추가(북마크 설정)
            bookmarkRepository.save(BookmarkEntity.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .build());
            return "북마크가 설정되었습니다.";
        }
    }

    // 특정 사용자의 북마크 목록을 리스트로 조회(최신순)
    @Override
    public List<BookmarkResponseDTO> getBookmarkedEvents(Long userId) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(BookmarkEntity::toDTO).toList();
    }

    @Override
    public Long getBookmarkCountByEvent(Long eventId) {
        return bookmarkRepository.countByEventId(eventId);
    }

    @Override
    public List<Long> getUserIdsByEvent(Long eventId) {
        return bookmarkRepository.findUserIdsByEventId(eventId);
    }


    @Override
    public List<BookmarkResponseDTO> getBookmarkedEvents() {
        return bookmarkRepository.findAllByOrderByBookmarkCountDesc()
                .stream().map(BookmarkEntity::toDTO).toList();
    }
}
