package life.eventory.event.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(name = "CreatedEventDTO", description = "생성된 행사 본문 DTO")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreatedEventInfoDTO {
    @Schema(
            description = "생성된 행사 본문(마크다운)",
            example = """
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
                  
                  여러분의 많은 관심과 참여를 기다립니다! 🎶✨
                  """
    )
    private String description;
    @Schema(description = "생성된 해시태그 목록", example = "[\"야시장\",\"음악\",\"대구축제\"]")
    private List<String> hashtags;
}
