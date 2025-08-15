package life.eventory.event.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.event.dto.MultipartFileResource;
import life.eventory.event.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
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
        ServiceInstance imageInstance = getImageInstance();

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/api/image")
                .build()
                .toUri();

        // 로컬 테스트 코드 예시
//        URI uri = UriComponentsBuilder.fromUri(URI.create("https://gateway.gamja.cloud"))
//                .path("/api/image")
//                .build()
//                .toUri();

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
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send request to Image-Server", e);
        }
    }

    @Override
    public void deletePoster(Long posterId) {
        ServiceInstance imageInstance = getImageInstance();

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
    private ServiceInstance getImageInstance() {
        if (discoveryClient == null) {
            throw new IllegalStateException("discoveryClient is null");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("IMAGE");
        log.info("Found {} IMAGE-SERVER instances", instances != null ? instances.size() : 0);

        if (instances == null || instances.isEmpty()) {
            log.error("No Image-Server instances available");
            throw new IllegalStateException("No Image-Server instances available");
        }

        return instances.get(0);
    }

    @Override
    public void existUser(Long userId) {
        ServiceInstance imageInstance = getUserInstance();

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
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send request to User-Server", e);
        }
    }

    // User 인스턴스 조회
    private ServiceInstance getUserInstance() {
        if (discoveryClient == null) {
            throw new IllegalStateException("discoveryClient is null");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("USER");
        log.info("Found {} USER-SERVER instances", instances != null ? instances.size() : 0);

        if (instances == null || instances.isEmpty()) {
            log.error("No User-Server instances available");
            throw new IllegalStateException("No User-Server instances available");
        }

        return instances.get(0);
    }
}
