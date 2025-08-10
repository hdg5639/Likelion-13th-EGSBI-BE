package life.eventory.user.service;

import life.eventory.user.dto.UserInfoResponse;
import life.eventory.user.dto.UserLocationRequest;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.dto.UserUpdateRequest;

public interface UserService {
    void signup(UserSignUpRequest request);
    UserInfoResponse getUserByEmail(String email);
    UserUpdateRequest update(UserUpdateRequest request);
    UserLocationRequest location(UserLocationRequest request);
}
