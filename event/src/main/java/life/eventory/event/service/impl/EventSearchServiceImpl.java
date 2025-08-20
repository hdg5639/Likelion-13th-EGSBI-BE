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
public class EventSearchServiceImpl implements EventSearchService {

    private final FullTextSearch fullTextSearch;
    private final JPAQueryFactory queryFactory;
    private final EventMapper mapper;

    public Page<EventDTO> searchFulltext(String rawQuery, Pageable pageable,
                                         @Nullable EventSearchCond extra) {
        String booleanQ = toBooleanQuery(rawQuery);
        if (booleanQ.isBlank()) return Page.empty(pageable);

        Pageable pageOnly = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());

        Page<Long> idPage = fullTextSearch.searchFulltextIds(booleanQ, pageOnly);
        if (idPage.isEmpty()) return Page.empty(pageable);

        List<Long> idsInOrder = idPage.getContent();

        QEvent e = QEvent.event;
        QTag t = QTag.tag;

        var fetched = queryFactory
                .selectFrom(e)
                .leftJoin(e.tags, t).fetchJoin()
                .where(
                        e.id.in(idsInOrder)
                )
                .distinct()
                .fetch();

        Map<Long, Event> byId = fetched.stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        List<EventDTO> ordered = idsInOrder.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(mapper::toDTO)
                .toList();

        return new PageImpl<>(ordered, pageable, idPage.getTotalElements());
    }

    private String toBooleanQuery(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();
        if (trimmed.isBlank()) return "";

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"([^\"]+)\"|(\\S+)").matcher(trimmed);

        java.util.List<String> parts = new java.util.ArrayList<>();
        while (m.find()) {
            String phrase = m.group(1);
            String word   = m.group(2);

            if (phrase != null) {
                String p = phrase.replaceAll("[+\\-~()><*]", " ").trim();
                if (!p.isBlank()) parts.add("\"" + p + "\"");
            } else if (word != null) {
                String w = word.replaceAll("[+\\-~()><\"*]", "");
                if (!w.isBlank()) parts.add(w + "*");
            }
        }
        return String.join(" ", parts);
    }
}
