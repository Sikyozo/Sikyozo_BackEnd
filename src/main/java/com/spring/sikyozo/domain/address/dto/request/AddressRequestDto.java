package com.spring.sikyozo.domain.address.dto.request;

import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    @NotBlank(message = "주소를 입력해 주세요.")
    private String addressName;

    @Size(max = 100, message = "요청사항은 100자 이하로 입력해 주세요.")
    private String request;

    public static Address toEntity(AddressRequestDto dto, User user) {
        return Address.builder()
                .user(user)
                .addressName(dto.addressName)
                .request(dto.request)
                .build();
    }
}
