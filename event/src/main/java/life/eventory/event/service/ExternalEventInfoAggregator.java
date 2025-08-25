package life.eventory.event.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import jakarta.annotation.PostConstruct;
import life.eventory.event.dto.external.ConvertAddress;
import life.eventory.event.dto.external.DaeguEventDTO;
import life.eventory.event.entity.Event;
import life.eventory.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalEventInfoAggregator {
    private final RestTemplate restTemplate;
    private final EventRepository eventRepository;
    private final EventTagService eventTagService;

    @Value("${geocoding.api}")
    private String geocodingApi;

    private List<DaeguEventDTO> getDaeguEvent() {
        // 로컬 테스트 코드 예시
        URI uri = UriComponentsBuilder.fromUri(URI.create("https://dgfca.or.kr"))
                .path("/api/daegu/cultural-events")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<List<DaeguEventDTO>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {}
                    );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to get daegu event");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to 대구문화예술진흥원", e);
        }
    }

    // 외부 행사 정보 변환 및 DB 저장 / 매일 0시 업데이트
    @PostConstruct
    @Scheduled(cron = "0 0 0 1 * *")
    public void transformAndSaveEvents() throws IOException, InterruptedException, ApiException {
        final LocalDate cutOff = LocalDate.of(2025, 1, 1);

        for (DaeguEventDTO daeguEventDTO :
                getDaeguEvent().stream()
                    .filter(e -> e.getStart_date().isAfter(cutOff)) // 2025년 이후로 필터
                    .sorted(Comparator.comparing(DaeguEventDTO::getStart_date).reversed()) // 최신순 정렬
                    .toList()
                .subList(0, 50)) // 50개만 남김
        {

            // 이미 존재하는 정보인지 확인
            if (eventRepository.existsExternalEvent(daeguEventDTO.getSubject()))
                continue;

            //주소 변환
            ConvertAddress convertAddress = convertToDetail(daeguEventDTO.getPlace(), daeguEventDTO.getEvent_area());

            if (convertAddress != null) {
                Event externalEvent = Event.builder()
                        .organizerId(0L) // 외부 행사는 0 으로 고정
                        .name(daeguEventDTO.getSubject())
                        .posterId(null)
                        .description(daeguEventDTO.getContent())
                        .startTime(toStartOfDay(daeguEventDTO.getStart_date()))
                        .endTime(toStartOfDay(daeguEventDTO.getEnd_date()))
                        .address(convertAddress.getAddress())
                        .latitude(convertAddress.getLatitude())
                        .longitude(convertAddress.getLongitude())
                        .createTime(LocalDateTime.now())
                        .entryFee(parseEntryFee(daeguEventDTO.getPay()))
                        .build();

                eventTagService.setEventHashtags(
                        eventRepository.save(externalEvent).getId(),
                        List.of(daeguEventDTO.getEvent_gubun())
                );
            }
        }
    }

    // 자정기준 변환 LocalDate -> LocalDateTime
    private static LocalDateTime toStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    // 주소, 좌표 변환
    private ConvertAddress convertToDetail(String place, String event_area) throws IOException, InterruptedException, ApiException {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(geocodingApi)
                .build();

        GeocodingResult[] results = GeocodingApi.geocode(context, place + " " + event_area)
                .components(ComponentFilter.administrativeArea("대구광역시"))
                .language("ko") // 한글 주소
                .await();

        if (results.length == 0) {
            return null;
        }

        LatLng location = results[0].geometry.location;
        String address = results[0].formattedAddress;

        return ConvertAddress.builder()
                .address(address)
                .latitude(location.lat)
                .longitude(location.lng)
                .build();
    }

    // 입장료 변환
    private Integer parseEntryFee(String pay) {
        if (pay == null || pay.isBlank()) return 0;

        var matcher = java.util.regex.Pattern.compile("(\\d+)")
                .matcher(pay.replaceAll(",", ""));

        List<Integer> prices = new ArrayList<>();
        while (matcher.find()) {
            prices.add(Integer.parseInt(matcher.group(1)));
        }

        return prices.isEmpty() ? 0 : Collections.max(prices);
    }
}
