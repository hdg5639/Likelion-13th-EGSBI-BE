package life.eventory.user.service.impl;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import jakarta.transaction.Transactional;
import life.eventory.user.dto.MultipartFileResource;
import life.eventory.user.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
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
@RequiredArgsConstructor
@Transactional
public class CommunicationServiceImpl implements CommunicationService {
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    @Override
    public Long uploadProfile(MultipartFile file) throws IOException {
        ServiceInstance imageInstance = getImageInstance();

        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/v1/image")
                .build()
                .toUri();

        if(restTemplate == null) {
            log.error("RestTemplate is null");
            throw new IllegalStateException("RestTemplate is not initialized");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartFileResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            log.info("Sending profile image to image-server: {}", file.getOriginalFilename());

            ResponseEntity<Long> response = restTemplate.postForEntity(uri, requestEntity, Long.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Profile image uploaded successfully. ID: {}", response.getBody());
                return response.getBody();
            }

            log.error("Failed to upload profile image. Response status: {}", response.getStatusCode());
            throw new IllegalStateException("Failed to upload profile image");
        } catch (Exception e) {
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send profile image to Image-Server", e);
        }
    }

    @Override
    public void deleteProfile(Long imageId) {
        ServiceInstance imageInstance = getImageInstance();

        // 요청 url 생성
        URI uri = UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/v1/image/" + imageId)
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

            throw new IllegalStateException("Failed to delete profile");
        } catch (Exception e) {
            imageErrorLog(e);
            throw new IllegalStateException("Failed to send request to Image-Server", e);
        }
    }

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

    private void imageErrorLog(Exception e) {
        log.error("Exception occurred while calling IMAGE-SERVER", e);
        log.error("Exception type: {}", e.getClass().getSimpleName());
        log.error("Exception message: {}", e.getMessage());
    }

    @Override
    public boolean organizerExists (Long organizerId) {
        ServiceInstance organizerInstance = getOrganizerInstance();

        URI uri = UriComponentsBuilder.fromUri(organizerInstance.getUri())
                .path("/v1/event/exist/organizer/{organizerId}")
                .buildAndExpand(organizerId)
                .toUri();

        ResponseEntity<Boolean> response = restTemplate.getForEntity(uri, Boolean.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new IllegalStateException("Failed to verify event existence");
        }
    }

    private ServiceInstance getOrganizerInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances("EVENT");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No EVENT-SERVICE instances available");
        }
        return instances.get(0);
    }

    @Override
    public List<Long> getUserIds(Long eventId) {
        ServiceInstance bookmarkInstance = getBookmarkInstance();

        URI uri = UriComponentsBuilder.fromUri(bookmarkInstance.getUri())
                .path("/v1/activity/participation/{eventId}")
                .buildAndExpand(eventId)
                .toUri();

        ResponseEntity<List<Long>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new IllegalStateException("Failed to fetch bookmarked userIds");
        }
    }

    private ServiceInstance getBookmarkInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances("ACTIVITY");
        if (instances == null || instances.isEmpty()) {
            log.error("No ACTIVITY-SERVICE instances available");
            throw new IllegalStateException("No ACTIVITY-SERVICE instances available");
        }
        return instances.get(0);
    }

    @Override
    public List<Long> UpcomingEventIds() {
        ServiceInstance eventInstance = getEventInstance();

        URI uri = UriComponentsBuilder.fromUri(eventInstance.getUri())
                .path("/v1/event/upcoming")
                .build()
                .toUri();

        ResponseEntity<List<Long>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {});

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new IllegalStateException("Failed to fetch upcoming events");
        }
    }

    private ServiceInstance getEventInstance() {
        List<ServiceInstance> instances = discoveryClient.getInstances("EVENT");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No EVENT service instances available");
        }
        return instances.get(0);
    }

    public String getImageUri(Long imageId) {
        ServiceInstance imageInstance = getImageInstance();

        return UriComponentsBuilder.fromUri(imageInstance.getUri())
                .path("/v1/image/{id}")
                .buildAndExpand(imageId)
                .toUriString();
    }
}
