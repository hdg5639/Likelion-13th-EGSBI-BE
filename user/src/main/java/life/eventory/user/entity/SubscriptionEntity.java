package life.eventory.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "t_subscription",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "organizerId"})
)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long organizerId;

    private String organizerName;
    private String organizerNickname;
    private Long profileImageId;
    private String profileImageUri;
}
