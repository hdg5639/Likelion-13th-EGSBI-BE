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
        EventDTO e = communicationService.getEvent(eventId);

        if (userId != null) {
            communicationService.addHistory(userId, new HistoryRequest(e.getId(), e.getName(), e.getPosterId()));
        }

        return chatClient.build()
                .prompt()
                .system("""
            너는 간결하게 한국어로 답한다. 마크다운만 반환한다.
            [규칙]
            - 상단 카드(날짜/시간/장소/가격/해시태그)의 값을 그대로 나열/반복하지 말 것.
            - 이벤트의 '느낌/특징/차별점/추천 대상/관람 팁'만 추려라.
            - 과한 수식어, 느낌표, 이모지 금지. 불필요한 숫자/주소 언급 금지.
            - 총 4줄만 출력한다: 1) 한 줄 요약 2) 하이라이트 3) 이런 분께 추천 4) 관람 팁.
            """)
                .user(u -> u.text("""
            아래 이벤트를 요약하되, 형식은 정확히 다음처럼 지켜라.

            출력 형식(정확히 4줄):
            한 줄 요약: <핵심을 1문장 35자 내>
            - 하이라이트: <공연/행사의 차별점 1가지만>
            - 이런 분께: <대상/취향 1~2개>
            - 관람 팁: <준비물/좌석/동선 등 1가지>

            참고 정보(반복 금지):
            - 이름: {name}
            - 내용: {description}
            - 기간: {start} ~ {end}
            - 위치: {address}
            - 입장료: {entryFee}
            - 해시태그: {hashtag}
            """)
                        .param("name", e.getName())
                        .param("description", e.getDescription())
                        .param("start", e.getStartTime())
                        .param("end", e.getEndTime())
                        .param("address", e.getAddress())
                        .param("entryFee", e.getEntryFee())
                        .param("hashtag", e.getHashtags())
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

    @Override
    public String createComment(String prompt) {
        try {
            String raw = chatClient.build()
                    .prompt()
                    .system("""
                        너는 한국어로 답해.
                        규칙:
                        - 입력 키워드/행사 정보를 바탕으로 '한 줄 코멘트'만 생성
                        - 최대 28자 내로 자연스럽게
                        - 마침표/이모지 과다 금지, 과장 표현 지양
                        - 출력은 텍스트 1문장만 (JSON/설명/따옴표/코드블록 금지)
                        """)
                    .user(u -> u.text("""
                        한 줄 코멘트를 만들어줘.
                        참고 정보:
                        {prompt}
                        """).param("prompt", prompt))
                    .call()
                    // 라이브러리에 따라 .content() 또는 .entity(String.class) 중 하나 사용
                    .content();

            String comment = postProcessOneLine(raw);
            return comment.isBlank() ? "취향에 맞춘 추천 행사들이에요" : comment;
        } catch (Exception e) {
            // 로그 남기고 폴백
            // log.warn("createComment failed", e);
            return "취향에 맞춘 추천 행사들이에요";
        }
    }

    private String postProcessOneLine(String s) {
        if (s == null) return "";
        // 줄바꿈/탭 → 공백
        String cleaned = s.replaceAll("[\\r\\n\\t]+", " ").trim();
        // 양끝 따옴표 제거
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\"")) ||
                (cleaned.startsWith("“") && cleaned.endsWith("”")) ||
                (cleaned.startsWith("‘") && cleaned.endsWith("’")) ||
                (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
        }
        // 공백 압축
        cleaned = cleaned.replaceAll("\\s+", " ");

        // 최대 길이 제한 (코드포인트 단위로 안전하게 자르기)
        if (cleaned.codePointCount(0, cleaned.length()) > 28) {
            cleaned = substringByCodePoint(cleaned);
        }

        // 문장부호 살짝 정리
        cleaned = cleaned.replaceAll("[.!?…]+$", ""); // 끝의 과한 문장부호 제거
        return cleaned;
    }

    private String substringByCodePoint(String s) {
        if (s == null) return "";
        int endIndex = s.offsetByCodePoints(0, Math.min(28, s.codePointCount(0, s.length())));
        return s.substring(0, endIndex).trim();
    }

}
