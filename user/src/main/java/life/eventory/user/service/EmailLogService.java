package life.eventory.user.service;

import life.eventory.user.entity.EmailLogEntity;

import java.util.List;

public interface EmailLogService {
    List<EmailLogEntity> getLogsByEmail(String email);
}
