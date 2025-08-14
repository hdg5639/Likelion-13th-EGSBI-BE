package life.eventory.user.service;

import life.eventory.user.dto.UserInfoResponse;
import life.eventory.user.dto.UserLocationRequest;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    void signup(UserSignUpRequest request, MultipartFile file) throws IOException;
    UserInfoResponse getUserByEmail(String email);
    UserUpdateRequest update(UserUpdateRequest request);
    UserLocationRequest location(UserLocationRequest request);
}
