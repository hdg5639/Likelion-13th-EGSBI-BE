package life.eventory.user.service;

import life.eventory.user.dto.*;
import life.eventory.user.dto.login.LoginResponse;
import life.eventory.user.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserSignUpRequest signup(UserSignUpRequest request, MultipartFile file) throws IOException;
    UserInfoResponse getUserById(Long userId);
    UserUpdateResponse update(UserUpdateRequest request, MultipartFile file) throws IOException;
    UserLocationRequest location(UserLocationRequest request);
    boolean UserExist(Long id);
    UserLocationResponse getUserLocation(Long id);
    LoginResponse authenticate(String email, String password);
    void deleteLocation(String email);
    ResponseEntity<Long> checkByEmail(String email);
    UserEntity getUserById_Id(Long userId);
}
