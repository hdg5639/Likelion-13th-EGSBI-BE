package life.eventory.activity.entity;

import jakarta.persistence.*;
import life.eventory.activity.dto.review.ReviewResponseDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="t_review")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long eventId;

    @Lob // 제한 없는 텍스트
    private String content; // 리뷰 내용

    @CreationTimestamp
    private LocalDateTime createdAt; // 리뷰 작성일

    private Integer rating; // 별점

    public ReviewResponseDTO toDTO() {
        return ReviewResponseDTO.builder()
                .userId(userId)
                .eventId(eventId)
                .content(content)
                .createdAt(createdAt)
                .rating(rating)
                .build();
    }

}
