package life.eventory.aggregator.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwaggerConfigRefreshInterceptor implements HandlerInterceptor {

    private final DynamicSwaggerRegistry registry;

    // 과도한 호출 방지 (기본 30초 쿨다운)
    private final Duration minInterval = Duration.ofSeconds(5);
    private final AtomicLong lastRefreshed = new AtomicLong(0L);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if ("GET".equals(request.getMethod()) && "/v3/api-docs/swagger-config".equals(uri)) {
            long now = System.currentTimeMillis();
            long last = lastRefreshed.get();
            if (now - last > minInterval.toMillis()) {
                try {
                    registry.refresh();
                    lastRefreshed.set(now);
                    log.info("Swagger URLs refreshed on UI load");
                } catch (Exception e) {
                    log.warn("Swagger refresh failed on UI load: {}", e.getMessage());
                }
            }
        }
        return true; // 계속 진행
    }
}
