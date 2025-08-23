package life.eventory.user.service.impl;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import life.eventory.user.entity.EmailEntity;
import life.eventory.user.entity.EmailLogEntity;
import life.eventory.user.entity.UserEntity;
import life.eventory.user.repository.EmailLogRepository;
import life.eventory.user.repository.EmailRepository;
import life.eventory.user.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailServiceImpl implements EmailService {
    private static final long VERIFICATION_CODE_EXPIRY_MINUTES = 3;
    private final EmailRepository emailRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;
    private final CommunicationService communicationService;

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public void saveVerificationCode(String email, String code) {
        EmailEntity entity = emailRepository.findByEmail(email).orElse(null);

        if (entity != null && entity.isVerified()) return;
        if (entity == null) {
            entity = EmailEntity.builder()
                    .email(email)
                    .build();
        }

        entity.setVerificationCode(code);
        entity.setCodeGeneratedTime(LocalDateTime.now());
        entity.setVerified(false);
        emailRepository.save(entity);
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        String subject = "[Eventory] 이메일 인증 코드 발송";
        String content = String.format(
                "<html>" +
                        "<body style='font-family: Arial, sans-serif; color: #333; font-size: 14px; line-height: 1.6;'>" +
                        "<p style='margin-bottom: 1em;'>안녕하세요, <span style='color: #28a745; font-weight: bold;'>Eventory</span> 입니다.</p>" +
                        "<p style='font-size: 18px; margin-bottom: 1em;'><span style='color: #3399FF; font-weight: bold;'>인증 코드</span>는 다음과 같습니다.</p>" +
                        "<div style='font-size:14px;'>&nbsp;</div>" +
                        "<p style='font-size: 24px; font-weight: bold; background: #f0f0f0; padding: 10px; border-radius: 5px; width: max-content; margin-bottom: 2em;'>%s</p>" +


                        "<p style='margin-bottom: 1em;'>감사합니다. Eventory 드림</p>" +
                        "</body>" + "</html>", code);
        sendEmail(email, subject, content);
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        Optional<EmailEntity> optEmail = emailRepository.findByEmail(email);
        if (optEmail.isEmpty()) return false;

        EmailEntity entity = optEmail.get();
        if (entity.getVerificationCode() == null || entity.isVerified()) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = entity.getCodeGeneratedTime().plusMinutes(VERIFICATION_CODE_EXPIRY_MINUTES);
        if (now.isAfter(expiryTime)) return false;

        if (entity.getVerificationCode().equals(inputCode)) {
            entity.setVerified(true);
            entity.setVerificationCode(null);
            entity.setCodeGeneratedTime(null);
            emailRepository.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkVerified(String email) {
        return emailRepository.findByEmail(email)
                .map(EmailEntity::isVerified)
                .orElse(false);
    }

    @Override
    public boolean sendEmail(String email, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void notifyNew(Long organizerId) {
        List<Long> userIds = subscriptionService.getUser(organizerId);

        for (Long userId : userIds) {
            UserEntity user = userService.getUserById_Id(userId);
            String userEmail = user.getEmail();
            if (!checkVerified(userEmail)) continue;

            String subject = "[Eventory] 행사 등록 알림 발송";
            String content = String.format(
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #333; font-size: 14px; line-height: 1.6;'>" +
                            "<p>안녕하세요 <span style='font-size: 18px; font-weight: bold;'>%s</span>님, <span style='color: #28a745; font-weight: bold;'>Eventory</span> 입니다.</p>" +

                            "<p style='margin-top: 2em;'>구독하신 행사 담당자가 " +
                            "<span style='font-size: 18px; color: #d9534f; font-weight: bold;'>새로운 행사</span>를 등록했습니다.</p>" +

                            "<p style='margin-top: 2em;'>지금 바로&nbsp;&nbsp;&nbsp;" +
                            "<a href='https://eventory.life' style='font-size: 18px; color: #1a73e8; font-weight: bold; text-decoration: none;'>eventory.life</a>&nbsp;&nbsp;&nbsp;에서 확인해보세요!</p>" +

                            "<p>감사합니다. Eventory 드림</p>" +
                            "</body>" +
                            "</html>", user.getName());
            boolean success = sendEmail(userEmail, subject, content);

            if (success) {
                EmailLogEntity log = EmailLogEntity.builder()
                        .userId(userId)
                        .organizerId(organizerId)
                        .email(userEmail)
                        .sendTime(LocalDateTime.now())
                        .build();
                emailLogRepository.save(log);
            }
        }
    }

    @Override
    public void notifyUpdate(Long eventId) {
        List<Long> userIds = communicationService.getUserIds(eventId);

        for (Long userId : userIds) {
            UserEntity User = userService.getUserById_Id(userId);
            String userEmail = User.getEmail();
            if (!checkVerified(userEmail)) continue;

            String subject = "[Eventory] 행사 수정 알림 발송";
            String content = String.format(
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; color: #333; font-size: 14px; line-height: 1.6;'>" +
                            "<p>안녕하세요 <span style='font-size: 18px; font-weight: bold;'>%s</span>님, <span style='color: #28a745; font-weight: bold;'>Eventory</span> 입니다.</p>" +

                            "<p style='margin-top: 2em;'>북마크 하신 행사 정보가 " +
                            "<span style='font-size: 18px; color: #d9534f; font-weight: bold;'>수정</span>되었습니다.</p>" +

                            "<p style='margin-top: 2em;'>지금 바로&nbsp;&nbsp;&nbsp;" +
                            "<a href='https://eventory.life' style='font-size: 18px; color: #1a73e8; font-weight: bold; text-decoration: none;'>eventory.life</a>&nbsp;&nbsp;&nbsp;에서 확인해보세요!</p>" +

                            "<p>감사합니다. Eventory 드림</p>" +
                            "</body>" +
                            "</html>", User.getName());
            boolean success = sendEmail(userEmail, subject, content);

            if (success) {
                EmailLogEntity log = EmailLogEntity.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .email(userEmail)
                        .sendTime(LocalDateTime.now())
                        .build();
                emailLogRepository.save(log);
            }
        }
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void notifyUpcoming() {
        try {
            List<Long> upcomingEventIds = communicationService.UpcomingEventIds();
            if (upcomingEventIds == null || upcomingEventIds.isEmpty()) return;

            for (Long eventId : upcomingEventIds) {
                List<Long> userIds = communicationService.getUserIds(eventId);
                for (Long userId : userIds) {
                    UserEntity User = userService.getUserById_Id(userId);
                    String userEmail = User.getEmail();
                    if (!checkVerified(userEmail)) continue;

                    String subject = "[Eventory] 행사 시작 전 알림 발송";
                    String content = String.format(
                            "<html>" +
                                    "<body style='font-family: Arial, sans-serif; color: #333; font-size: 14px; line-height: 1.6;'>" +
                                    "<p>안녕하세요 <span style='font-size: 18px; font-weight: bold;'>%s</span>님, <span style='color: #28a745; font-weight: bold;'>Eventory</span> 입니다.</p>" +

                                    "<p style='margin-top: 2em;'>북마크 하신 행사가 " +
                                    "<span style='font-size: 18px; color: #d9534f; font-weight: bold;'>곧 시작됩니다</span>.</p>" +

                                    "<p style='margin-top: 2em;'>지금 바로&nbsp;&nbsp;&nbsp;" +
                                    "<a href='https://eventory.life' style='font-size: 18px; color: #1a73e8; font-weight: bold; text-decoration: none;'>eventory.life</a>&nbsp;&nbsp;&nbsp;에서 확인해보세요!</p>" +

                                    "<p>감사합니다. Eventory 드림</p>" +
                                    "</body>" +
                                    "</html>", User.getName());
                    boolean success = sendEmail(userEmail, subject, content);

                    if (success) {
                        EmailLogEntity log = EmailLogEntity.builder()
                                .userId(userId)
                                .eventId(eventId)
                                .email(userEmail)
                                .sendTime(LocalDateTime.now())
                                .build();
                        emailLogRepository.save(log);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to send upcoming event notifications", e);
        }
    }
}
