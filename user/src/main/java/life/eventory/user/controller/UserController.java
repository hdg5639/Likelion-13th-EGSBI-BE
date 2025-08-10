package life.eventory.user.controller;

import life.eventory.user.dto.UserInfoResponse;
import life.eventory.user.dto.UserLocationRequest;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.dto.UserUpdateRequest;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> info(@RequestParam String email){
        UserInfoResponse userInfoResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userInfoResponse);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserUpdateRequest> update(@RequestBody UserUpdateRequest request) {
        UserUpdateRequest userUpdateRequest = userService.update(request);
        return ResponseEntity.ok(userUpdateRequest);
    }

    @PostMapping("/location")
    public ResponseEntity<UserLocationRequest> location(@RequestBody UserLocationRequest request) {
        UserLocationRequest userLocationRequest = userService.location(request);
        return ResponseEntity.ok(userLocationRequest);
    }
}
