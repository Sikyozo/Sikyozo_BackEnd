package com.spring.sikyozo.domain.user.sevice;

import com.spring.sikyozo.domain.user.dto.request.SignUpRequestDto;
import com.spring.sikyozo.domain.user.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.UserDuplicateEmailException;
import com.spring.sikyozo.domain.user.exception.UserDuplicateUsernameException;
import com.spring.sikyozo.domain.user.exception.UserPasswordMismatchException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public MessageResponseDto signUp(SignUpRequestDto dto) {
        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(dto.getUsername());
        if (checkUsername.isPresent())
            throw new UserDuplicateUsernameException();

        // 이메일 중복 확인
        Optional<User> checkEmail = userRepository.findByEmail(dto.getEmail());
        if (checkEmail.isPresent())
            throw new UserDuplicateEmailException();

        // 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck()))
            throw new UserPasswordMismatchException();

        // 비밀번호 인코딩
        String password = passwordEncoder.encode(dto.getPassword());

        // 사용자 정보 저장
        User user = User.builder()
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(password)
                .role(UserRole.CUSTOMER)
                .build();

        userRepository.save(user);

        return new MessageResponseDto("회원가입을 성공했습니다.");
    }
}
