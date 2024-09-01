package com.spring.sikyozo.domain.address.dto.response;

import com.spring.sikyozo.domain.address.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
    private UUID id;
    private Long userId;
    private String addressName;
    private String request;

    public static AddressResponseDto fromEntity(Address entity) {
        return AddressResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .addressName(entity.getAddressName())
                .request(entity.getRequest())
                .build();
    }
}
