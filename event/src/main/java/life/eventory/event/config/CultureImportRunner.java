package life.eventory.event.config;

import life.eventory.event.service.CultureEventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CultureImportRunner {
    private static final Logger log = LoggerFactory.getLogger(CultureImportRunner.class);

    @Bean
    public ApplicationRunner runCultureImport(
            CultureEventMapper service,
            @Value("${culture.import.enabled:true}") boolean enabled,
            @Value("${culture.import.organizer-id:1}") long organizerId
    ) {
        return args -> {
            if (!enabled) {
                log.info("Culture import disabled.");
                return;
            }
            try {
                int inserted = service.importTop50FromResource(organizerId);
                log.info("SoulCulture import finished. inserted={}", inserted);
            } catch (Exception e) {
                log.warn("SoulCulture import failed: {}", e.getMessage(), e);
            }
        };
    }
}
