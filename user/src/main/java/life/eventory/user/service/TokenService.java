package life.eventory.user.service;

import life.eventory.user.dto.login.LoginResponse;

public interface TokenService {
    LoginResponse issueAccessToken(Long userId);
}
