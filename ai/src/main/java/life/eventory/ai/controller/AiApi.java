package life.eventory.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.ai.dto.AiComment;
import life.eventory.ai.dto.AiEventDTO;
import life.eventory.ai.dto.CreatedEventDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI API", description = "AI 서버 API")
@RequestMapping("/api/ai")
public interface AiApi {
    @Operation(
            summary = "행사 요약 생성 API",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            ## 수성구 야시장 요약
                                                            
                                                            **이벤트 이름**: 수성구 야시장 \s
                                                            **내용**: 2025 한여름 밤의 푸드 & 뮤직 페스티벌
                                                            
                                                            - **장소**: 한강 시민공원 여의도 지구
                                                            - **기간**: 2025년 8월 22일(금) ~ 8월 24일(일)
                                                            - **시간**: 매일 오후 3시 ~ 밤 11시
                                                            
                                                            ### 주요 프로그램
                                                            - **라이브 공연**: 윤하, 루시드 그루브, 어반파파 등
                                                            - **푸드트럭**: 다양한 음식 제공, 총 50대 참여
                                                            - **체험 활동**: 포토존, 요리 클래스, 아트 마켓, 야간 불꽃놀이
                                                            
                                                            ### 입장료
                                                            - **일반권**: 5,000원
                                                            - **패키지권**: 15,000원 (푸드 쿠폰 포함)
                                                            - **무료 입장**: 초등학생 이하, 65세 이상, 반려견 동반 가능
                                                            
                                                            **축제 경험**: 미각, 청각, 시각을 모두 즐길 수 있는 특별한 여름 밤!"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/{eventId}")
    ResponseEntity<String> createEventSummary(
            @RequestHeader(name = "X-User-Id", required = false) Long userId,
            @Parameter(description = "행사 ID", example = "1")
            @PathVariable Long eventId);

    @Operation(
            summary = "행사 본문 생성 API",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "행사 본문 생성 예시",
                                                    value = """
                                                            # 수성구 야시장 안내
                                                            
                                                            안녕하세요! 🎉
                                                            
                                                            여름의 끝자락, 특별한 밤을 여러분께 선사할 **수성구 야시장**이 찾아옵니다! 다채로운 먹거리와 즐길 거리가 가득한 이곳에서 잊지 못할 추억을 만들어보세요.
                                                            
                                                            ## 행사 정보
                                                            - **기간**: 2025년 8월 31일 (토) 저녁 7시 30분 ~ 9시 30분
                                                            - **위치**: 대구 수성구 국채보상로 123
                                                            - **입장료**: 20,000원
                                                            
                                                            ## 행사 내용
                                                            이번 야시장에서는 다양한 **푸드트럭**이 준비되어 입맛을 사로잡는 특별한 메뉴를 제공합니다. 또한, 현장에서는 **라이브 공연**이 열려 음악과 함께하는 즐거운 시간을 만끽할 수 있습니다. 다양한 볼거리와 먹거리가 여러분을 기다리고 있습니다!
                                                            
                                                            특별한 여름밤, 사랑하는 친구와 가족과 함께 수성구 야시장에서 즐거운 시간을 보내세요. 많은 참여 부탁드립니다!
                                                            
                                                            ## 해시태그
                                                            #야시장음악 #대구축제
                                                            
                                                            여러분의 많은 관심과 참여를 기다립니다! 🎶✨"""
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/description")
    ResponseEntity<CreatedEventDTO> createEventDescription(
            @Parameter(description = "AI 행사 생성 정보 DTO")
            @RequestBody AiEventDTO aiEventDTO);

    @Operation(
            summary = "행사 추천 코멘트 생성 API",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "생성 성공",
                            content = @Content(
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @PostMapping("/comment")
    ResponseEntity<String> createComment(
            @Parameter(description = "추천 코멘트 프롬프트")
            @RequestBody AiComment prompt);

    @Operation(
            summary = "개인 리뷰 종합 평가 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "생성 성공",
                            content = @Content(
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/review/summary")
    ResponseEntity<String> createReviewSummary(
            @RequestHeader(name = "X-User-Id") Long userId);
}
