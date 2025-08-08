package life.eventory.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.eventory.image.dto.ResponseDTO;
import life.eventory.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Tag(name = "Image API", description = "이미지 업로드 및 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;

    @Value("${image.upload-dir}")
    private String uploadDir;

    @Operation(
            summary = "이미지 업로드",
            description = "Multipart 파일을 업로드합니다. 업로드된 이미지의 ID를 응답으로 반환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "업로드 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "파일 없음 또는 오류")
            }
    )
    @PostMapping
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");

        return ResponseEntity.ok(imageService.upload(file));
    }

    // 이미지 조회
    @Operation(
            summary = "이미지 조회",
            description = "ID를 기반으로 저장된 이미지를 다운로드합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이미지 반환"),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 이미지 없음")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "이미지 ID", example = "1")
            @PathVariable Long id) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(imageService.findNameById(id)).normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }
}
