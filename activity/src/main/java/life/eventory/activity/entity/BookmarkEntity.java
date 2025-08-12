package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.BookmarkDTO;
import lombok.*;

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

    public BookmarkDTO toDTO() {
        return BookmarkDTO.builder()
                .id(id)
                .userId(userId)
                .eventId(eventId)
                .build();
    }
}
