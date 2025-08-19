package life.eventory.ai.service.impl;

import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.CreatedEventDTO;
import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.dto.activity.HistoryRequest;
import life.eventory.ai.service.AiService;
import life.eventory.ai.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final ChatClient.Builder chatClient;
    private final CommunicationService communicationService;

    @Override
    public String createEventSummary(Long userId, Long eventId) {
        EventDTO eventDTO = communicationService.getEvent(eventId);

        if (userId != null) {
            communicationService.addHistory(
                    userId,
                    new HistoryRequest(
                            eventDTO.getId(),
                            eventDTO.getName(),
                            eventDTO.getPosterId()
                    )
            );
        }

        return chatClient.build()
                .prompt()
                .system("너는 간결하게 한국어로 답해. 마크다운만 반환해.")
                .user(u ->
                        u.text("""
                        다음 이벤트 정보를 보고 간단 요약을 만들어줘.
                        - 이름: {name}
                        - 내용: {description}
                        - 기간: {start} ~ {end}
                        - 위치: {address}
                        - 입장료: {entryFee}
                        - 해시태그: {hashtag}
                        """)
                        .param("name", eventDTO.getName())
                        .param("description", eventDTO.getDescription())
                        .param("start", eventDTO.getStartTime())
                        .param("end", eventDTO.getEndTime())
                        .param("address", eventDTO.getAddress())
                        .param("entryFee", eventDTO.getEntryFee())
                        .param("hashtag", eventDTO.getHashtags())
                    )
                .call()
                .content();
    }

    @Override
    public CreatedEventDTO createDescription(AiEventDTO aiEventDTO) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E) a h시 m분");
        String start = aiEventDTO.getStartTime() != null ? aiEventDTO.getStartTime().format(fmt) : "";
        String end   = aiEventDTO.getEndTime()   != null ? aiEventDTO.getEndTime().format(fmt)   : "";

        return chatClient.build()
                .prompt()
                .system("너는 한국어로 답해. 최고의 완성도와 풍부한 내용으로 작성해. 다음 이벤트 정보를 바탕으로 CreatedEventDTO JSON을 생성해. description은 행사 소개글(마크다운), hashtags는 '#'을 제외하고 JSON 배열 문자열로 반환.")
                .user(u -> u.text("""
                        다음 이벤트 정보를 보고 행사 안내 게시물 본문 내용 만들어줘.
                        - 이름: {name}
                        - 간단 행사 내용: {description}
                        - 기간: {start} ~ {end}
                        - 위치: {address}
                        - 입장료: {entryFee}
                        """)
                        .param("name", aiEventDTO.getName())
                        .param("description", aiEventDTO.getDescription())
                        .param("start", start)
                        .param("end", end)
                        .param("address", aiEventDTO.getAddress())
                        .param("entryFee", aiEventDTO.getEntryFee() == null ? "" : aiEventDTO.getEntryFee().toString())
                )
                .call()
                .entity(CreatedEventDTO.class);
    }
}
