package life.eventory.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Long upload(MultipartFile file) throws IOException;
    String findNameById(Long id);
    Boolean deleteById(Long id);
}
