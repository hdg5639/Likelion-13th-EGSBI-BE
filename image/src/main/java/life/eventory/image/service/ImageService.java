package life.eventory.image.service;

import life.eventory.image.dto.ReturnDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    ReturnDTO upload(MultipartFile file) throws IOException;
    String findNameById(Long id);
    Boolean deleteById(Long id);
}
