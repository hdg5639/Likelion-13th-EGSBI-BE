package life.eventory.aggregator.config;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicSwaggerRegistry {

    private final EurekaClient eurekaClient;
    private final SwaggerUiConfigProperties swaggerUiConfig;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${gateway.url}")
    private String gateway;

    private static final Set<String> EXCLUDES = Set.of("GATEWAY", "AGGREGATOR");

    /** 런타임에 스웨거 URL들을 다시 스캔해서 교체 등록 */
    public synchronized List<SwaggerUiConfigProperties.SwaggerUrl> refresh() {
        RestTemplate rt = restTemplateBuilder
                .connectTimeout(java.time.Duration.ofSeconds(1))
                .readTimeout(java.time.Duration.ofSeconds(1))
                .build();

        // serviceName(소문자) → /{service}/api-docs
        Map<String, String> map = new LinkedHashMap<>();

        eurekaClient.getApplications().getRegisteredApplications().forEach(app -> {
            String raw = app.getName();                 // 대문자
            if (EXCLUDES.contains(raw)) return;

            String service = raw.toLowerCase();         // 게이트웨이 경로는 소문자 사용 권장
            String url = gateway + "/" + service + "/api-docs";

            // 서비스당 한 번만 체크(여러 인스턴스 중복 방지)
            if (map.containsKey(service)) return;

            try {
                ResponseEntity<String> resp = rt.getForEntity(url, String.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    map.put(service, url);
                }
            } catch (Exception e) {
                log.debug("[{}] api-docs 확인 실패: {}", service, e.getMessage());
            }
        });

        // springdoc 설정에 반영
        List<SwaggerUiConfigProperties.SwaggerUrl> urls = map.entrySet().stream().map(e -> {
            SwaggerUiConfigProperties.SwaggerUrl su = new SwaggerUiConfigProperties.SwaggerUrl();
            su.setName(e.getKey()); // 좌측 드롭다운에 보일 이름
            su.setUrl(e.getValue());
            return su;
        }).toList();

        // Set을 쓰면 UI 드롭다운 순서가 바뀔 수 있으므로 LinkedHashSet 유지
        swaggerUiConfig.setUrls(new LinkedHashSet<>(urls));
        log.info("Swagger URLs refreshed: {}", map.keySet());
        return urls;
    }
}
