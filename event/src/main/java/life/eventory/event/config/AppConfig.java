package life.eventory.event.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    public RestTemplate generalRestTemplate() {
        return new RestTemplate();
    }
}
