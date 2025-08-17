package life.eventory.user.controller;

import life.eventory.user.controller.api.UserAPI;
import life.eventory.user.dto.*;
import life.eventory.user.dto.login.LoginRequest;
import life.eventory.user.dto.login.LoginResponse;
import life.eventory.user.service.TokenService;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserAPI {
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public ResponseEntity<UserSignUpRequest> signup(
            @RequestPart(value = "user") UserSignUpRequest request,
            @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        UserSignUpRequest signUpRequest = userService.signup(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(signUpRequest);
    }

    @Override
    public ResponseEntity<UserInfoResponse> info(@RequestParam String email){
        UserInfoResponse userInfoResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userInfoResponse);
    }

    @Override
    public ResponseEntity<UserUpdateRequest> update(@RequestPart(value = "user") UserUpdateRequest request,
                                                    @RequestPart(value = "image", required = false) MultipartFile file) throws IOException{
        UserUpdateRequest userUpdateRequest = userService.update(request, file);
        return ResponseEntity.ok(userUpdateRequest);
    }

    @Override
    public ResponseEntity<UserLocationRequest> location(@RequestBody UserLocationRequest request) {
        UserLocationRequest userLocationRequest = userService.location(request);
        return ResponseEntity.ok(userLocationRequest);
    }

    @Override
    public boolean checkUserExists(@PathVariable Long id) {
        return userService.UserExist(id);
    }

    @Override
    public ResponseEntity<UserLocationResponse> locationInfo(@PathVariable Long id) {
        UserLocationResponse location =  userService.getUserLocation(id);
        return ResponseEntity.ok(location);
    }

    @Override
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(userService.authenticate(request.getEmail(), request.getPassword()));
    }

    @Override
    public ResponseEntity<LoginResponse> renew(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(tokenService.issueAccessToken(Long.parseLong(userId)));
    }
}
