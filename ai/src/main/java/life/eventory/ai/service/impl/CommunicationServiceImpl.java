package life.eventory.ai.service.impl;

import life.eventory.ai.dto.EventDTO;
import life.eventory.ai.dto.activity.HistoryRequest;
import life.eventory.ai.dto.activity.HistoryResponse;
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
        ServiceInstance eventInstance = getServerInstance("EVENT");

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

    @Override
    public void addHistory(Long userId, HistoryRequest historyRequest) {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/history/add")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<HistoryRequest> requestEntity = new HttpEntity<>(historyRequest, headers);

        try {
            ResponseEntity<HistoryResponse> response =
                    restTemplate.exchange(uri,
                            HttpMethod.POST,
                            requestEntity,
                            HistoryResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

            throw new IllegalStateException("Failed to create Event History");
        } catch (Exception e) {
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send request to Activity-Server", e);
        }
    }

    // 서버 인스턴스 조회
    private ServiceInstance getServerInstance(String serverName) {
        if (discoveryClient == null) {
            throw new IllegalStateException("discoveryClient is null");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(serverName.toUpperCase());
        log.info("Found {} {}-SERVER instances", instances != null ? instances.size() : 0, serverName);

        if (instances == null || instances.isEmpty()) {
            log.error("No {}-SERVER instances available", serverName);
            throw new IllegalStateException("No " + serverName + "-SERVER instances available");
        }

        return instances.get(0);
    }
}
