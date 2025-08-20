package life.eventory.user.controller;

import life.eventory.user.controller.api.EmailAPI;
import life.eventory.user.service.EmailService;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class EmailController implements EmailAPI {
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public ResponseEntity<String> sendCode(@RequestParam String email) {
        ResponseEntity<Long> userCheckResponse = userService.checkByEmail(email);
        if (!userCheckResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("사용자가 존재하지 않습니다.");
        }
        String code = emailService.generateVerificationCode();
        emailService.saveVerificationCode(email, code);
        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    @Override
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String inputCode) {
        boolean result = emailService.verifyCode(email, inputCode);
        if (result) {
            return ResponseEntity.ok("이메일 인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 틀렸거나 만료되었습니다.");
        }
    }

    @Override
    public ResponseEntity<Boolean> checkVerified(@RequestParam String email) {
        return ResponseEntity.ok(emailService.checkVerified(email));
    }

    @Override
    public ResponseEntity<Void> notifyNew(@PathVariable Long organizerId) {
        emailService.notifyNew(organizerId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> notifyEventUpdate(@PathVariable Long eventId) {
        emailService.notifyUpdate(eventId);
        return ResponseEntity.ok().build();
    }
}
