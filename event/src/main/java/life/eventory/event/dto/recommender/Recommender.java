package life.eventory.event.dto.recommender;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class Recommender {
    private final Map<Long, Double> tagWeights; // tagId -> weight
    private final Set<Long> excludeEventIds;    // 이미 본/참여/북마크 등 제외할 이벤트
    private final List<Long> topTagIds;         // 상위 태그 id (예: 10개)
}

