package life.eventory.aggregator.config;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DynamicSwaggerConfig implements ApplicationRunner {

    private final EurekaClient eurekaClient;
    private final SwaggerUiConfigProperties swaggerUiConfig;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${gateway.url}")
    private String gateway;

    @Override
    public void run(ApplicationArguments args) {
        List<SwaggerUiConfigProperties.SwaggerUrl> urls = new ArrayList<>();
        RestTemplate restTemplate = restTemplateBuilder.build();

        // Eureka에 등록된 모든 서비스 이름
        eurekaClient.getApplications().getRegisteredApplications().forEach(app -> {
            String serviceName = app.getName();

            // gateway, aggregator는 제외
            if (serviceName.equalsIgnoreCase("GATEWAY") || serviceName.equalsIgnoreCase("AGGREGATOR")) {
                return;
            }

            app.getInstances().forEach(instance -> {
                String name = serviceName.toLowerCase();
                String url = gateway + "/" + serviceName + "/api-docs";

                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        SwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new SwaggerUiConfigProperties.SwaggerUrl();
                        swaggerUrl.setName(name);
                        swaggerUrl.setUrl(url);
                        urls.add(swaggerUrl);
                    }
                } catch (Exception e) {
                    System.out.println("[" + name + "] 문서 요청 실패: " + e.getMessage());
                }
            });
        });

        swaggerUiConfig.setUrls(new HashSet<>(urls));
    }
}

