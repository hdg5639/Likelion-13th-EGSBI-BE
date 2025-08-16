package life.eventory.ai.service.impl;

import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.service.AiService;
import life.eventory.ai.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final ChatClient.Builder chatClient;
    private final CommunicationService communicationService;

    @Override
    public String createEventSummary(Long eventId) {
        EventDTO eventDTO = communicationService.getEvent(eventId);

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
    public String createDescription(AiEventDTO aiEventDTO) {
        return chatClient.build()
                .prompt()
                .system("너는 한국어로 답해. 최대한 완성도있게 풍부한 내용으로 작성해. 마크다운만 반환해.")
                .user(u ->
                        u.text("""
                        다음 이벤트 정보를 보고 행사 안내 게시물 본문 내용 만들어줘.
                        - 이름: {name}
                        - 간단 행사 내용: {description}
                        - 기간: {start} ~ {end}
                        - 위치: {address}
                        - 입장료: {entryFee}
                        - 해시태그: {hashtag}
                        """)
                        .param("name", aiEventDTO.getName())
                        .param("description", aiEventDTO.getDescription())
                        .param("start", aiEventDTO.getStartTime())
                        .param("end", aiEventDTO.getEndTime())
                        .param("address", aiEventDTO.getAddress())
                        .param("entryFee", aiEventDTO.getEntryFee())
                        .param("hashtag", aiEventDTO.getHashtags())
                )
                .call()
                .content();
    }
}
