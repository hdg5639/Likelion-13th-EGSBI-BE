package life.eventory.user.repository;

import life.eventory.user.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    Optional<EmailEntity> findByEmail(String email);
    @Query("select e.verified from EmailEntity e where e.email = :email")
    Boolean checkVerified(@Param("email") String email);
}
