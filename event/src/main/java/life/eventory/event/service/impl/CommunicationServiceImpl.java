package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.activity.BookmarkResponse;
import life.eventory.event.dto.activity.HistoryResponse;
import life.eventory.event.dto.activity.ParticipationResponse;
import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
import life.eventory.event.dto.MultipartFileResource;
import life.eventory.event.service.CommunicationService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    @Override
    public Long uploadPoster(MultipartFile file) throws IOException {
        // 배포 요청 코드
        ServiceInstance imageInstance = getServerInstance("IMAGE");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/image")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 파일 바디 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartFileResource(file));

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Long> response = restTemplate.postForEntity(uri, requestEntity, Long.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to upload poster");
        } catch (Exception e) {
            errorLog(e, "IMAGE");
            throw new IllegalStateException("Failed to send request to Image-Server", e);
        }
    }

    @Override
    public void deletePoster(Long posterId) {
        ServiceInstance imageInstance = getServerInstance("IMAGE");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/image/" + posterId)
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<Map<String, String>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.DELETE,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful() &&
                    Objects.requireNonNull(response.getBody()).get("status").equals("success")) {
                return;
            }

            throw new IllegalStateException("Failed to delete poster");
        } catch (Exception e) {
            errorLog(e, "IMAGE");
            throw new IllegalStateException("Failed to send request to Image-Server", e);
        }
    }

    @Override
    public void existUser(Long userId) {
        ServiceInstance imageInstance = getServerInstance("USER");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/user/exist/" + userId)
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<Boolean> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<Boolean> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            Boolean.class);

            if (response.getStatusCode().is2xxSuccessful() &&
                    Objects.requireNonNull(response.getBody())) {
                return;
            }

            throw new IllegalStateException("Failed to check exist of user");
        } catch (Exception e) {
            errorLog(e, "USER");
            throw new IllegalStateException("Failed to send request to User-Server", e);
        }
    }

    @Override
    public CreatedEventInfoDTO createAiDescription(AiEventDTO aiEventDTO) {
        ServiceInstance imageInstance = getServerInstance("AI");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/ai/description")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<AiEventDTO> requestEntity = new HttpEntity<>(aiEventDTO, headers);

        try {
            ResponseEntity<CreatedEventInfoDTO> response =
                    restTemplate.exchange(uri,
                            HttpMethod.POST,
                            requestEntity,
                            CreatedEventInfoDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create Ai-Description");
        } catch (Exception e) {
            errorLog(e, "AI");
            throw new IllegalStateException("Failed to send request to Ai-Server", e);
        }
    }

    // 기록 조회
    @Override
    public List<HistoryResponse> getHistory(Long userId) {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/history/list")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "viewedAt")
        );

        // 요청 HTTP 엔티티 생성
        return historyFunc(uri, pageable, headers);
    }

    @Override
    public List<HistoryResponse> getHistoryPage(Long userId, Pageable pageable) {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/history/list")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        return historyFunc(uri, pageable, headers);
    }

    private List<HistoryResponse> historyFunc(URI uri, Pageable pageable, HttpHeaders headers) {
        HttpEntity<Pageable> requestEntity = new HttpEntity<>(pageable, headers);

        try {
            ResponseEntity<List<HistoryResponse>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create Event History");
        } catch (Exception e) {
            errorLog(e, "ACTIVITY");
            throw new IllegalStateException("Failed to send request to Activity-Server", e);
        }
    }

    // 북마크 조회
    @Override
    public List<BookmarkResponse> getBookmark(Long userId) {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/bookmark/list")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<List<BookmarkResponse>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create Event History");
        } catch (Exception e) {
            errorLog(e, "ACTIVITY");
            throw new IllegalStateException("Failed to send request to Activity-Server", e);
        }
    }

    @Override
    public LinkedHashMap<Long, Long> getAllBookmarkedEvents() {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/bookmark/all")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<LinkedHashMap<Long, Long>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create Event History");
        } catch (Exception e) {
            errorLog(e, "ACTIVITY");
            throw new IllegalStateException("Failed to send request to Activity-Server", e);
        }
    }

    // 참여 조회
    @Override
    public List<ParticipationResponse> getParticipation(Long userId) {
        ServiceInstance imageInstance = getServerInstance("ACTIVITY");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/activity/participation/list")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", userId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<List<ParticipationResponse>> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create Event History");
        } catch (Exception e) {
            errorLog(e, "ACTIVITY");
            throw new IllegalStateException("Failed to send request to Activity-Server", e);
        }
    }

    @Override
    public String getComment(String prompt) {
        ServiceInstance imageInstance = getServerInstance("AI");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/ai/comment")
                .build()
                .toUri();

        // 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<String> requestEntity = new HttpEntity<>(prompt, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(uri,
                            HttpMethod.GET,
                            requestEntity,
                            String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            throw new IllegalStateException("Failed to create AI Comment");
        } catch (Exception e) {
            errorLog(e, "AI");
            throw new IllegalStateException("Failed to send request to Ai-Server", e);
        }
    }

    @Override
    public void notiNewEvent(Long organizerId) {
        ServiceInstance imageInstance = getServerInstance("USER");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/user/email/notify/new/" + organizerId)
                .build()
                .toUri();

        sendNotification(uri);
    }

    @Override
    public void notiUpdateEvent(Long eventId) {
        ServiceInstance imageInstance = getServerInstance("USER");

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/user/email/notify/update/{eventId}" + eventId)
                .build()
                .toUri();

        sendNotification(uri);
    }

    private void sendNotification(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 HTTP 엔티티 생성
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<Void> response =
                    restTemplate.exchange(uri,
                            HttpMethod.POST,
                            requestEntity,
                            Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return;
            }

            throw new IllegalStateException("Failed to notification Event");
        } catch (Exception e) {
            errorLog(e, "USER");
            throw new IllegalStateException("Failed to send request to User-Server", e);
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

    // 에러 로그
    private void errorLog(Exception e, String serverName) {
        log.error("Exception occurred while calling {}-SERVER", serverName, e);
        log.error("Exception type: {}", e.getClass().getSimpleName());
        log.error("Exception message: {}", e.getMessage());
    }
}
