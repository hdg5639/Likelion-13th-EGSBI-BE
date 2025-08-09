package life.eventory.user.service;

import life.eventory.user.dto.UserSignUpRequest;

public interface UserService {
    void signup(UserSignUpRequest request);
}
