package life.eventory.image.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.image.dto.ReturnDTO;
import life.eventory.image.entity.Image;
import life.eventory.image.repository.ImageRepository;
import life.eventory.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${image.upload-dir}")
    private String uploadDir;


    @Override
    public ReturnDTO upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("빈 파일");
        }

        // 1. 확장자 추출
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        // 2. UUID 기반 파일명 생성
        String storedFilename = UUID.randomUUID() + "." + extension;

        // 3. 파일 저장
        Path filepath = Paths.get(uploadDir).resolve(storedFilename);
        Files.createDirectories(filepath.getParent());
        Files.copy(file.getInputStream(), filepath);

        // 4. DB 저장
        Image image = imageRepository.save(Image.builder()
                .storedFileName(storedFilename)
                .build());

        return new ReturnDTO(image.getId());
    }

    @Override
    public String findNameById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("존재하지 않음"))
                .getStoredFileName();
    }

    @Override
    public Boolean deleteById(Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    Path filePath = Paths.get(uploadDir).resolve(image.getStoredFileName());

                    if (!Files.exists(filePath)) {
                        imageRepository.delete(image);
                        return true;
                    }

                    try {
                        Files.deleteIfExists(filePath);
                        imageRepository.delete(image);
                    } catch (IOException e) {
                        log.error("파일 삭제 실패: {}", e.getMessage());
                        return false;
                    }

                    return true;
                })
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 이미지"));
    }
}
