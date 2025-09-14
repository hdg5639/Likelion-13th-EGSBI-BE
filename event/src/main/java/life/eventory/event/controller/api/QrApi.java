package life.eventory.event.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Tag(name = "QR API", description = "QR API")
@RequestMapping("/v1/event/qr")
public interface QrApi {

    @Operation(
            summary = "참여용 QR 코드(PNG) 발급",
            description = "eventId를 담은 참여 URL을 QR로 변환 후 이미지 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "QR PNG ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Long.class,
                                    examples = "1"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "이벤트 없음", content = @Content)
    })
    @PostMapping("/join")
    ResponseEntity<Long> joinQr(
            @Parameter(description = "이벤트 ID", example = "1000", required = true)
            @RequestParam Long eventId)
            throws UnsupportedEncodingException;

    @Operation(
            summary = "참여용 QR 코드(PNG) ID 조회",
            description = "해당 행사의 QR 이미지 ID를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "QR PNG ID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Long.class,
                                    examples = "1"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "이벤트 없음", content = @Content)
    })
    @GetMapping
    ResponseEntity<Long> getQrId(
            @Parameter(description = "이벤트 ID", example = "1000", required = true)
            @RequestParam Long eventId);
}
