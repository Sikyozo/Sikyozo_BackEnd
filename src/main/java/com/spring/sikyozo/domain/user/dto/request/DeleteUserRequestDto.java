package com.spring.sikyozo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRequestDto {
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String currentPassword;
}