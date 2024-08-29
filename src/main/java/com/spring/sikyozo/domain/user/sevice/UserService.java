package com.spring.sikyozo.domain.user.sevice;

import com.spring.sikyozo.domain.user.dto.request.SignUpRequestDto;
import com.spring.sikyozo.domain.user.dto.request.UserInfoUpdateRequestDto;
import com.spring.sikyozo.domain.user.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.user.dto.response.UserResponseDto;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.*;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public MessageResponseDto signUp(SignUpRequestDto dto) {
        // 회원 중복 확인
        checkUsernameDuplication(dto.getUsername());

        // 닉네임 중복 확인
        checkNicknameDuplication(dto.getNickname());

        // 이메일 중복 확인
        checkEmailDuplication(dto.getEmail());

        // 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck()))
            throw new UserPasswordMismatchException();

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        userRepository.save(SignUpRequestDto.toEntity(dto, encodedPassword));

        return new MessageResponseDto("회원가입을 성공했습니다.");
    }

    // 사용자 정보 조회
    public UserResponseDto findUserInfo(Long id) {
        User currentUser = securityUtil.getCurrentUser();

        if (!currentUser.getId().equals(id) && !isAdmin(currentUser))
            throw new AccessDeniedException();

        User targetUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return UserResponseDto.fromEntity(targetUser);
    }

    // 사용자 정보 전체 조회 (MANAGER, MASTER)
    public Page<UserResponseDto> findAllUsers(int page, int size, String search, String sortBy, String sortDirection) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser))
            throw new AccessDeniedException();

        // 페이지 크기 제한
        if (size != 10 && size != 30 && size != 50)
            size = 10; // 기본 페이지 크기로 고정

        // 정렬 방향 설정
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 검색어가 있으면 username을 기준으로 검색
        Page<User> users;

        if (search != null && !search.isEmpty())
            users = userRepository.findByUsernameContaining(search, pageable);
        else users = userRepository.findAll(pageable);

        return users.map(UserResponseDto::fromEntity);
    }
    
    // 사용자 정보 수정
    public MessageResponseDto updateUserInfo(Long id, UserInfoUpdateRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!currentUser.getId().equals(id) && !isAdmin(currentUser))
            throw new AccessDeniedException();

        User targetUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // 닉네임 중복 확인
        if (dto.getNickname() != null && !dto.getNickname().equals(targetUser.getNickname())) {
            checkNicknameDuplication(dto.getNickname());
            currentUser.updateNickname(dto.getNickname());
        }

        // 이메일 중복 확인
        if (dto.getEmail() != null && !dto.getEmail().equals(targetUser.getEmail())) {
            checkEmailDuplication(dto.getEmail());
            currentUser.updateEmail(dto.getEmail());
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            // 기존 비밀번호 확인
            if (!passwordEncoder.matches(dto.getCurrentPassword(), targetUser.getPassword()))
                throw new UserPasswordMismatchException();

            // 새 비밀번호 확인
            if (!dto.getNewPassword().equals(dto.getNewPasswordCheck()))
                throw new UserPasswordMismatchException();

            // 새 비밀번호 인코딩 및 저장
            targetUser.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(targetUser);

        return new MessageResponseDto("사용자 정보가 성공적으로 수정되었습니다.");
    }

    // MANAGER, MASTER 권한 체크
    private boolean isAdmin(User user) {
        return user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.MASTER);
    }

    // 회원 중복 확인(username)
    private void checkUsernameDuplication(String username) {
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new UserDuplicateUsernameException();
        }
    }

    // 닉네임 중복 확인
    private void checkNicknameDuplication(String nickname) {
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new UserDuplicateNicknameException();
        }
    }

    // 이메일 중복 확인
    private void checkEmailDuplication(String email) {
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new UserDuplicateEmailException();
        }
    }
}
