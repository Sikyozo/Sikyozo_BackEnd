package com.spring.sikyozo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateRequestDto {
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해 주세요.")
    private String nickname;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    // 기존 비밀번호 확인용 필드
    @NotBlank(message = "기존 비밀번호를 입력해 주세요.")
    private String currentPassword;

    // 새 비밀번호
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*])[A-Za-z\\d~!@#$%^&*]{8,15}$",
            message = "비밀번호는 8자 이상 15자 이하, 알파벳 대소문자, 숫자, 특수문자를 포함해 주세요.")
    private String newPassword;

    // 새 비밀번호 확인
    @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.")
    private String newPasswordCheck;
}
