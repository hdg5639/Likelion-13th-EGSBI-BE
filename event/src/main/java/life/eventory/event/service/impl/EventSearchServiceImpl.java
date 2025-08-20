package life.eventory.event.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.search.EventSearchCond;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.QEvent;
import life.eventory.event.entity.QTag;
import life.eventory.event.repository.FullTextSearch;
import life.eventory.event.service.EventMapper;
import life.eventory.event.service.EventSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.lang.Nullable;                // ✅ 스프링 Nullable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventSearchServiceImpl implements EventSearchService {               // ✅ 클래스명 PascalCase

    private final FullTextSearch fullTextSearch;
    private final JPAQueryFactory queryFactory;     // QueryDSL
    private final EventMapper mapper;               // entity -> DTO

    public Page<EventDTO> searchFulltext(String rawQuery, Pageable pageable,
                                         @Nullable EventSearchCond extra) {
        String booleanQ = toBooleanQuery(rawQuery);
        if (booleanQ.isBlank()) return Page.empty(pageable);

        // ✅ 정렬 제거 (nativeQuery가 자체 ORDER BY를 가짐)
        Pageable pageOnly = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());

        Page<Long> idPage = fullTextSearch.searchFulltextIds(booleanQ, pageOnly);
        if (idPage.isEmpty()) return Page.empty(pageable);

        List<Long> idsInOrder = idPage.getContent();

        // 2) 실제 데이터 로드 (+ 추가 조건 필요시 extra 사용)
        QEvent e = QEvent.event;
        QTag t = QTag.tag;

        var fetched = queryFactory
                .selectFrom(e)
                .leftJoin(e.tags, t).fetchJoin()
                .where(
                        e.id.in(idsInOrder)
                        // 필요 시 추가 where:
                        // , extra != null && extra.getStartFrom() != null ? e.startTime.goe(extra.getStartFrom()) : null
                        // , extra != null && extra.getStartTo()   != null ? e.startTime.loe(extra.getStartTo())   : null
                )
                .distinct()
                .fetch();

        // 3) 원래 ID 순서대로 재정렬 후 DTO 매핑
        Map<Long, Event> byId = fetched.stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        List<EventDTO> ordered = idsInOrder.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(mapper::toDTO)
                .toList();

        return new PageImpl<>(ordered, pageable, idPage.getTotalElements());
    }

    // 공백으로 토큰 나눠 +토큰* 로 변환 (특수기호 제거)
    private String toBooleanQuery(String raw) {
        if (raw == null || raw.isBlank()) return "";
        return Arrays.stream(raw.trim().split("\\s+"))
                .map(token -> token.replaceAll("[+\\-~()><\"*]", "")) // 불필요 기호 제거
                .filter(s -> !s.isBlank())
                .map(s -> "+" + s + "*")                              // 접두사 매칭
                .collect(Collectors.joining(" "));
    }
}
