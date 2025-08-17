package life.eventory.user.service;

import life.eventory.user.dto.*;
import life.eventory.user.dto.login.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserSignUpRequest signup(UserSignUpRequest request, MultipartFile file) throws IOException;
    UserInfoResponse getUserByEmail(String email);
    UserUpdateRequest update(UserUpdateRequest request, MultipartFile file) throws IOException;
    UserLocationRequest location(UserLocationRequest request);
    boolean UserExist(Long id);
    UserLocationResponse getUserLocation(Long id);
    LoginResponse authenticate(String email, String password);
}
