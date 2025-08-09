package life.eventory.user.controller;

import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@RequestBody UserSignUpRequest request) {
        userService.signup(request);
    }
}
