package life.eventory.user.controller;

import life.eventory.user.dto.UserInfoResponse;
import life.eventory.user.dto.UserLocationRequest;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.dto.UserUpdateRequest;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(
            @RequestPart(value = "user") UserSignUpRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        userService.signup(request, file);
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
