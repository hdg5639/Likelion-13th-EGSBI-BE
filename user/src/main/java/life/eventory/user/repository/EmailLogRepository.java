package life.eventory.user.repository;

import life.eventory.user.entity.EmailLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLogEntity, Long> {
    List<EmailLogEntity> findByEmail(String email);
}
