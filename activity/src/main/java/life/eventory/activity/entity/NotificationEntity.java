package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.notification.NotificationResponseDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    private LocalDateTime createdAt;

    public NotificationResponseDTO toDTO() {
        return NotificationResponseDTO.builder()
                .userId(userId)
                .eventId(eventId)
                .createdAt(createdAt)
                .build();
    }
}
