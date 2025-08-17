package life.eventory.user.service;

public interface TokenService {
    String issueAccessToken(String userId);
}
