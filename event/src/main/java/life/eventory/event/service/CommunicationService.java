package life.eventory.event.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CommunicationService {
    Long uploadPoster(MultipartFile file) throws IOException;
    void deletePoster(Long posterId);
}
