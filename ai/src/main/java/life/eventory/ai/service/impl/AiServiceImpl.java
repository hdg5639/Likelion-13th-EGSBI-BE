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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            너는 간결하게 한국어로 답한다. 마크다운과 적절한 이모티콘을 반환한다.
            [규칙]
            - 상단 카드(날짜/시간/장소/가격/해시태그)의 값을 그대로 나열/반복하지 말 것.
            - 이벤트의 '느낌/특징/차별점/추천 대상/관람 팁'만 추려라.
            - 적절한 이모티콘 활용 허용.
            - 강조 등 마크다운 허용.
            - 총 4가지만 출력한다: 1) 요약 2) 하이라이트 3) 이런 분께 추천 4) 관람 팁.
            """)
                .user(u -> u.text("""
            아래 이벤트를 요약하되, 형식은 정확히 다음처럼 지켜라.

            출력 형식(정확히 4줄):
            <종합 핵심을 2~3문장, 문장 당 20자 이상 40자 이내>
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
                .system("""
                        너는 한국어 카피라이터다. 사실만 사용해 설득력 있고 생동감 있게 쓴다.
                        [원칙]
                        - 과장·추측·허위 금지. 입력에 없는 세부정보(출연진, 좌석, 혜택 등) 만들지 말 것.
                        - 본문은 마크다운으로, 읽기 흐름이 좋은 섹션 구성.
                        - 강조는 **굵게**와 목록 사용.
                        - 적절한 이모지와 #제목글 사용.
                        [출력형식]
                        반드시 JSON만 출력한다(코드블록·설명·주석 금지).
                        {
                          "description": "<마크다운 본문>",
                          "hashtags": ["태그1","태그2",...]
                        }
                        해시태그는 # 기호 없이 한글 소문자/영문 소문자 단어로만.
                        """)
                .user(u -> u.text("""
                        다음 정보를 바탕으로 CreatedEventDTO를 생성하라.
                        
                        [작성 가이드]
                        - 첫 문단: 행사 분위기/가치 제안 한 문장(20~40자).
                        - 섹션 1 "행사 소개": 특징·차별점 2~3문장.
                        - 섹션 2 "하이라이트": 불릿 3개(사실 기반).
                        - 섹션 3 "참여 팁": 준비물/관람 포인트 2개(입력에 근거한 일반 조언).
                        - 섹션 4 "유의사항": 정보가 없으면 "현장 공지에 따릅니다." 한 줄.
                        - 해시태그: 핵심 키워드 3~6개. # 제외. 입력에 없는 브랜드명/지명 생성 금지.
                        
                        [입력]
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
                        .param("entryFee", aiEventDTO.getEntryFee()==null? "": aiEventDTO.getEntryFee().toString())
                )

                .call()
                .entity(CreatedEventDTO.class);
    }

    @Override
    public String createUserReviewSummary(Long userId) {
        List<String> reviews = communicationService.getReviews(userId);

        if (reviews == null || reviews.isEmpty()) return "아직 리뷰가 부족해요";

        String formatted = reviews.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> "- " + s.replaceAll("\\s+", " "))
                .collect(Collectors.joining("\n"));

        try {
            return chatClient.build()
                    .prompt()
                    .system("""
                    너는 한국어로 답한다.
                    규칙:
                    - 아래 리뷰를 바탕으로 '한 줄 평가 종합'만 생성
                    - 28자 이내, 자연스럽게. 의미 유지가 우선이면 26~30자 범위 허용
                    - 날짜/가격/장소 등 메타정보 언급 금지
                    - 마침표/이모지/따옴표/코드블록/머리말 금지
                    - 출력은 '순수 텍스트 한 문장'만
                    """)
                    .user(u -> u.text("""
                    <리뷰 모음>
                    {reviews}
                    </리뷰 모음>

                    위 내용을 바탕으로 한 문장으로만 요약해줘.
                    """).param("reviews", formatted))
                    .call()
                    .content();
        } catch (Exception e) {
            return "아직 리뷰가 부족해요";
        }
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
                    .content();

            String comment = postProcessOneLine(raw);
            return comment.isBlank() ? "취향에 맞춘 추천 행사들이에요" : comment;
        } catch (Exception e) {
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
