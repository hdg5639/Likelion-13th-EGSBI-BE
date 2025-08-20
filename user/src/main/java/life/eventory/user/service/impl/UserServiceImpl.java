package life.eventory.user.service.impl;

import jakarta.transaction.Transactional;
import life.eventory.user.dto.*;
import life.eventory.user.dto.login.LoginResponse;
import life.eventory.user.entity.UserEntity;
import life.eventory.user.repository.UserRepository;
import life.eventory.user.service.CommunicationService;
import life.eventory.user.service.TokenService;
import life.eventory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommunicationService communicationService;
    private final TokenService tokenService;

    @Override
    public UserSignUpRequest signup(UserSignUpRequest request, MultipartFile file) throws IOException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Long profileImageId = null;
        if(file != null && !file.isEmpty()) {
            profileImageId = communicationService.uploadProfile(file);
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setProfileId(profileImageId);

        userRepository.save(user);

        return UserSignUpRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public UserInfoResponse getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        return UserInfoResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileId(user.getProfileId())
                .build();
    }

    @Override
    public UserUpdateResponse update(UserUpdateRequest request, MultipartFile file) throws IOException {
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
        if (! request.getProfileEnabled()) {
            communicationService.deleteProfile(user.getProfileId());
            user.setProfileId(null);
        } else {
            if (file != null && !file.isEmpty()) {
                Long profileImageId = communicationService.uploadProfile(file);
                // 기존 이미지 삭제
                if (user.getProfileId() != null)
                    communicationService.deleteProfile(user.getProfileId());

                user.setProfileId(profileImageId);
            }
        }

        return entityToUpdate(userRepository.save(user));
    }

    private UserUpdateResponse entityToUpdate(UserEntity userEntity) {
        return UserUpdateResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .nickname(userEntity.getNickname())
                .profileId(userEntity.getProfileId())
                .build();
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

    @Override
    public boolean UserExist(Long id){
        return userRepository.existsById(id);
    }

    @Override
    public ResponseEntity<Long> checkByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(user.getId()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public UserLocationResponse getUserLocation(Long id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자가 없음"));

        return UserLocationResponse.builder()
                .id(user.getId())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .address(user.getAddress())
                .build();
    }

    @Override
    public LoginResponse authenticate(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 이메일 정보"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호 정보");
        }
        LoginResponse response = tokenService.issueAccessToken(user.getId());
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setProfileId(user.getProfileId());
        return response;
    }

    @Override
    public void deleteLocation(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));

        if (user != null) {
            user.setLatitude(null);
            user.setLongitude(null);
            user.setAddress(null);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User with email " + email + " not found.");
        }
    }

    @Override
    public UserEntity getUserById_Id(Long userId) {
        return userRepository.findById(userId)
                .orElse(null);
    }
}
