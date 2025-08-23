package life.eventory.user.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.user.entity.EmailLogEntity;
import life.eventory.user.repository.EmailLogRepository;
import life.eventory.user.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailLogServiceImpl implements EmailLogService {
    private final EmailLogRepository emailLogRepository;

    @Override
    public List<EmailLogEntity> getLogsByEmail(String email) {
        return emailLogRepository.findByEmail(email);
    }
}
