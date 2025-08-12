package life.eventory.user.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.user.dto.UserInfoResponse;
import life.eventory.user.dto.UserLocationRequest;
import life.eventory.user.dto.UserSignUpRequest;
import life.eventory.user.dto.UserUpdateRequest;
import life.eventory.user.entity.UserEntity;
import life.eventory.user.repository.UserRepository;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signup(UserSignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setProfile(request.getProfile());

        userRepository.save(user);
    }

    @Override
    public UserInfoResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserInfoResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserUpdateRequest update(UserUpdateRequest request){
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
        }
        if (request.getName() != null && !request.getName().isBlank())
            user.setName(request.getName());
        if (request.getNickname() != null && !request.getNickname().isBlank())
            user.setNickname(request.getNickname());
        if (request.getPhone() != null && !request.getPhone().isBlank())
            user.setPhone(request.getPhone());
        if (request.getProfile() != null)
            user.setProfile(request.getProfile());

        userRepository.save(user);
        return request;
    }

    @Override
    public UserLocationRequest location(UserLocationRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (request.getLatitude() != null)
            user.setLatitude(request.getLatitude());
        if (request.getLongitude() != null)
            user.setLongitude(request.getLongitude());
        if (request.getAddress() != null && !request.getAddress().isBlank())
            user.setAddress(request.getAddress());

        userRepository.save(user);
        return request;
    }
}
