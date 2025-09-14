package life.eventory.aggregator.api;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import life.eventory.aggregator.config.DynamicSwaggerRegistry;

import java.util.List;

@RestController
@RequestMapping("/v1/internal/swagger")
@RequiredArgsConstructor
public class DynamicSwaggerController {

    private final DynamicSwaggerRegistry registry;

    @PostMapping("/refresh")
    public ResponseEntity<List<SwaggerUiConfigProperties.SwaggerUrl>> refresh() {
        return ResponseEntity.ok(registry.refresh());
    }
}
