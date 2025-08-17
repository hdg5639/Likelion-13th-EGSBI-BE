package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.ParticipationDTO;
import life.eventory.activity.dto.UserEventDTO;
import lombok.*;

@Entity
@Table(name="t_userevent")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    private String eventName;
    private String date;

    public UserEventDTO toDTO() {
        return UserEventDTO.builder()
                .id(id)
                .userId(userId)
                .eventId(eventId)
                .eventName(eventName)
                .date(date)
                .build();
    }
}
