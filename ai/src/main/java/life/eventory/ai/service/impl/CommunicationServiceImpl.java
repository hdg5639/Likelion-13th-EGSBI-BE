package life.eventory.ai.service.impl;

import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Override
    public EventDTO getEvent(Long eventId) {
        ServiceInstance eventInstance = getEventInstance();

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(eventInstance.getUri())
                .path("/api/event/info/" + eventId)
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<EventDTO> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            EventDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to delete poster");
        } catch (Exception e) {
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send request to Image-Server", e);
        }
    }

    private void imageErrorLog(Exception e) {
        log.error("Exception occurred while calling IMAGE-SERVER", e);
        log.error("Exception type: {}", e.getClass().getSimpleName());
        log.error("Exception message: {}", e.getMessage());
    }

    // Image 인스턴스 조회
    private ServiceInstance getEventInstance() {
        if (discoveryClient == null) {
            throw new IllegalStateException("discoveryClient is null");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("EVENT");
        log.info("Found {} EVENT-SERVER instances", instances != null ? instances.size() : 0);

        if (instances == null || instances.isEmpty()) {
            log.error("No Event-Server instances available");
            throw new IllegalStateException("No Event-Server instances available");
        }

        return instances.get(0);
    }
}
