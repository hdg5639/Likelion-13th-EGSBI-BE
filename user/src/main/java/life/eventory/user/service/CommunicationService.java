package life.eventory.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CommunicationService {
    Long uploadProfile(MultipartFile file) throws IOException;
    void deleteProfile(Long imageId);
    boolean organizerExists(Long eventId);
    String getImageUri(Long imageId);
}
