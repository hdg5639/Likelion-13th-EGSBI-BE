package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.bookmark.BookmarkResponseDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="t_bookmark")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Long bookmarkCount;

    public BookmarkResponseDTO toDTO() {
        return BookmarkResponseDTO.builder()
                .userId(userId)
                .eventId(eventId)
                .createdAt(createdAt)
                .bookmarkCount(bookmarkCount)
                .build();
    }
}
