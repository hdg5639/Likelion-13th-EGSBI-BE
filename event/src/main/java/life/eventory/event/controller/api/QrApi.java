package life.eventory.event.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "QR API", description = "QR API")
@RequestMapping("/api/event/qr")
public interface QrApi {
}
