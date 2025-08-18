package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.history.HistoryResponseDTO;
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

    private String eventName;
    private String eventThumbnail;

    // 조회 시각
    private LocalDateTime viewedAt;

    public HistoryResponseDTO toDTO() {
        return HistoryResponseDTO.builder()
                .userId(userId)
                .eventId(eventId)
                .eventName(eventName)
                .eventThumbnail(eventThumbnail)
                .viewedAt(viewedAt)
                .build();
    }
}
