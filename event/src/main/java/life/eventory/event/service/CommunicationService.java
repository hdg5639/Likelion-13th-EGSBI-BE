package life.eventory.event.service;

import life.eventory.event.dto.ai.AiEventDTO;
import life.eventory.event.dto.ai.CreatedEventInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CommunicationService {
    Long uploadPoster(MultipartFile file) throws IOException;
    void deletePoster(Long posterId);
    void existUser(Long userId);
    CreatedEventInfoDTO createAiDescription(AiEventDTO aiEventDTO);
}
