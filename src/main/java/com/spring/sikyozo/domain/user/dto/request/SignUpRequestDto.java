package com.spring.sikyozo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하로 입력해야 합니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해야 합니다.")
    private String nickname;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호 확인은 최소 8자 이상이어야 합니다.")
    private String passwordCheck;

    @NotBlank(message = "권한은 필수 입력 항목입니다.")
    private String role;
}
