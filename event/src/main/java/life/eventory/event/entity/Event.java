package life.eventory.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "t_event")
@Builder
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long organizerId;
    @Column(nullable = false)
    private String name;
    // Poster는 null 허용
    private Long posterId;
    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String description;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private Integer entryFee;
    @Column(nullable = false)
    private LocalDateTime createTime;
    @Column
    private Long qrImage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_event_tag",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "tag_id"})
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Tag> tags = new LinkedHashSet<>();
}