package life.eventory.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CommunicationService {
    Long uploadProfile(MultipartFile file) throws IOException;
    void deleteProfile(Long imageId);
    boolean organizerExists(Long eventId);
    List<Long> getUserIds(Long eventId);
    List<Long> UpcomingEventIds();
    String getImageUri(Long imageId);
}
