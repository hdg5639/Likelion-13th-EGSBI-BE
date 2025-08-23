package life.eventory.event.service;

import life.eventory.event.dto.EventDTO;
import life.eventory.event.dto.activity.EventBookmark;
import life.eventory.event.entity.Event;
import life.eventory.event.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "hashtags",
            expression = "java(tagsToNames(e.getTags()))")
    EventDTO toDTO(Event e);

    @Mapping(target = "hashtags",
            expression = "java(tagsToNames(e.getTags()))")
    @Mapping(target = "bookmarkCount", expression = "java(bookmarkCount)")
    EventBookmark toBookmark(Event e, long bookmarkCount);

    // ★ Set/List 무엇이 와도 받는 형태로 변경
    default List<String> tagsToNames(java.util.Collection<? extends Tag> tags) {
        if (tags == null || tags.isEmpty()) return java.util.List.of();
        return tags.stream().map(Tag::getName).toList();
    }
}

