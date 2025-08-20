package life.eventory.user.service;

public interface EmailService {
    String generateVerificationCode();
    void saveVerificationCode(String email, String code);
    void sendVerificationCode(String email, String code);
    boolean verifyCode(String email, String inputCode);
    boolean checkVerified(String email);
    boolean sendEmail(String email, String subject, String content);
    void notifyNew(Long organizerId);
    void notifyUpdate(Long eventId);
}
