package life.eventory.user.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.entity.UserEntity;
import life.eventory.user.repository.UserRepository;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void signup(UserSignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setProfile(request.getProfile());

        userRepository.save(user);
    }
}
