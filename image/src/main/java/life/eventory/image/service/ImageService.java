package life.eventory.image.service;

import life.eventory.image.dto.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    ResponseDTO upload(MultipartFile file) throws IOException;
    String findNameById(Long id);
}
