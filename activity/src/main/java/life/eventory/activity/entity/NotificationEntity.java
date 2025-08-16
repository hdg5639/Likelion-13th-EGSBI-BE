package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.NotificationDTO;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="t_notification")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    private LocalDateTime createdAt = LocalDateTime.now();

    public NotificationDTO toDTO() {
        return NotificationDTO.builder()
                .id(id)
                .userId(userId)
                .eventId(eventId)
                .createdAt(createdAt)
                .build();
    }
}
