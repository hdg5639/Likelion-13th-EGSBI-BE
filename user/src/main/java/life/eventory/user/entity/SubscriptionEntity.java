package life.eventory.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "구독 정보 Entity")
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "구독 ID", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Column(nullable = false)
    @Schema(description = "주최자 ID", example = "0")
    private Long organizerId;
}
