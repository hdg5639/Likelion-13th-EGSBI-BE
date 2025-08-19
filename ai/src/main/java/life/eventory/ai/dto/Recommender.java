package life.eventory.ai.dto;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Recommender {
    private Set<String> hashtags;
    private Set<Long> exclude;
}
