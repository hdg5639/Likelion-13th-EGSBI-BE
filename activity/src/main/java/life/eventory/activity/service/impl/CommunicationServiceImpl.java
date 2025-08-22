package life.eventory.activity.service.impl;

import life.eventory.activity.dto.event.EventResponse;
import life.eventory.activity.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    // 상위 100개 까지 조회
    @Override
    public List<EventResponse> getUserEvents(Long userId) {
        // 배포 요청 코드
        ServiceInstance imageInstance = getServerInstance();

        // 요청 헤더 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/event/" + userId)
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Pageable pageable = PageRequest.of(
                0,
                100,
                Sort.by(Sort.Direction.DESC, "viewedAt")
        );

        // 요청 HTTP 엔티티 생성
        HttpEntity<Pageable> requestEntity = new HttpEntity<>(pageable, headers);

        try {
            ResponseEntity<List<EventResponse>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful())
                return response.getBody();

            throw new IllegalStateException("Failed to get user event");
        } catch (Exception e) {
            errorLog(e);
            throw new IllegalStateException("Failed to send request to Event-Server", e);
        }
    }

    // 서버 인스턴스 조회
    private ServiceInstance getServerInstance() {
        if (discoveryClient == null) {
            throw new IllegalStateException("discoveryClient is null");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("EVENT".toUpperCase());
        log.info("Found {} {}-SERVER instances", instances != null ? instances.size() : 0, "EVENT");

        if (instances == null || instances.isEmpty()) {
            log.error("No {}-SERVER instances available", "EVENT");
            throw new IllegalStateException("No " + "EVENT" + "-SERVER instances available");
        }

        return instances.get(0);
    }

    // 에러 로그
    private void errorLog(Exception e) {
        log.error("Exception occurred while calling {}-SERVER", "EVENT", e);
        log.error("Exception type: {}", e.getClass().getSimpleName());
        log.error("Exception message: {}", e.getMessage());
    }
}
