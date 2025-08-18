package life.eventory.activity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="t_participation")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    private LocalDateTime joinedAt;
}
