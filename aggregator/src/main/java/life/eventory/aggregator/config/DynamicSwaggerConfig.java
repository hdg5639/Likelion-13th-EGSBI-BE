package life.eventory.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class DynamicSwaggerConfig implements ApplicationRunner {

    private final DynamicSwaggerRegistry registry;

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        registry.refresh();
    }

    @Scheduled(fixedDelay = 30_000) // 30초마다
    public void scheduledRefresh() { registry.refresh(); }
}

