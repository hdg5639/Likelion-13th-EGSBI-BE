package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.BookmarkDTO;
import life.eventory.activity.dto.HistoryDTO;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="t_history")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    // 조회 시각
    private LocalDateTime viewedAt;

    public HistoryDTO toDTO() {
        return HistoryDTO.builder()
                .id(id)
                .userId(userId)
                .eventId(eventId)
                .viewedAt(viewedAt)
                .build();
    }
}
