package life.eventory.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Tag(name = "Image API", description = "이미지 업로드 및 조회 API")
@RequestMapping("/api/image")
public interface ImageApi {

    @Operation(
            summary = "이미지 업로드",
            description = "Multipart 파일을 업로드합니다. 업로드된 이미지의 ID를 응답으로 반환합니다.",
            requestBody = @RequestBody(
                    content = @Content(mediaType = "multipart/form-data")
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "업로드 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Long.class,
                                            example = "1"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "파일 없음 또는 오류",
                            content = @Content()
                    )
            }
    )
    @PostMapping
    ResponseEntity<Long> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            MultipartFile file
    ) throws IOException;

    @Operation(
            summary = "이미지 조회",
            description = "ID를 기반으로 저장된 이미지를 다운로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "이미지 반환",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "해당 ID의 이미지 없음",
                            content = @Content()
                    )
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<Resource> getImage(
            @Parameter(description = "이미지 ID", example = "1")
            @PathVariable Long id
    ) throws IOException;

    @Operation(
            summary = "이미지 삭제",
            description = "ID를 기반으로 저장된 이미지를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "이미지 삭제 성공",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "해당 ID의 이미지 없음",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content()
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Map<String, String>> deleteImage(
            @Parameter(description = "이미지 ID", example = "1")
            @PathVariable Long id
    ) throws IOException;
}
