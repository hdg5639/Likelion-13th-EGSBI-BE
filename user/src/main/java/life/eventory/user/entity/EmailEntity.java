package life.eventory.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_email")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private boolean verified;
    private String verificationCode;
    private LocalDateTime codeGeneratedTime;
}
