package life.eventory.image.controller;

import life.eventory.image.dto.ReturnDTO;
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


@RestController
@RequiredArgsConstructor
public class ImageController implements ImageApi {

    private final ImageService imageService;

    @Value("${image.upload-dir}")
    private String uploadDir;

    // 이미지 업로드
    @Override
    public ResponseEntity<ReturnDTO> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(imageService.upload(file));
    }

    // 이미지 조회
    @Override
    public ResponseEntity<Resource> getImage(
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

    @Override
    public ResponseEntity<Boolean> deleteImage(
            @PathVariable Long id) throws IOException {
        return ResponseEntity.ok(imageService.deleteById(id));
    }
}
