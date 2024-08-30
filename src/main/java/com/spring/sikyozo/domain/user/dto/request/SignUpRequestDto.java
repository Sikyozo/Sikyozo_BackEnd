package com.spring.sikyozo.domain.user.dto.request;

import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "아이디를 입력해 주세요.")
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 4자 이상 10자 이하, 알파벳 소문자와 숫자를 포함해 주세요.")
    private String username;

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해 주세요.")
    private String nickname;

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*])[A-Za-z\\d~!@#$%^&*]{8,15}$",
            message = "비밀번호는 8자 이상 15자 이하, 알파벳 대소문자, 숫자, 특수문자를 포함해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordCheck;

    @NotBlank(message = "권한을 입력해 주세요.")
    @Pattern(regexp = "^(CUSTOMER|OWNER|MANAGER|MASTER)$", message = "유효한 권한을 입력해 주세요.")
    private String role;

    public static User toEntity(SignUpRequestDto dto, String encodedPassword) {
        return User.builder()
                .username(dto.username)
                .nickname(dto.nickname)
                .email(dto.email)
                .password(encodedPassword)
                .role(UserRole.valueOf(dto.role))
                .build();
    }
}
