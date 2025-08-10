package life.eventory.activity.service;

import jakarta.transaction.Transactional;
import life.eventory.activity.dto.BookmarkDTO;
import life.eventory.activity.entity.BookmarkEntity;
import life.eventory.activity.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
//    private final UserRepository userRepository;
//    private final EventRepository eventRepository;

    @Transactional
    public boolean toggleBookmark(Long userId, Long eventId) {
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
            return false;
        }
        else { // 북마크가 없어서 새로 추가(북마크 설정)
            bookmarkRepository.save(BookmarkEntity.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .build());
            return true;
        }
    }

        // 특정 사용자의 북마크 목록을 리스트로 조회
    public List<BookmarkDTO> getBookmarkedEvents(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream().map(BookmarkEntity::toDTO).toList();
    }
}
