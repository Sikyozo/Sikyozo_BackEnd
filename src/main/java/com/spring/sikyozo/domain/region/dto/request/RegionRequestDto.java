package com.spring.sikyozo.domain.region.dto.request;

import com.spring.sikyozo.domain.region.entity.Region;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDto {
    @NotBlank(message = "지역을 입력해 주세요.")
    private String regionName;

    public static Region toEntity(RegionRequestDto dto) {
        return Region.builder()
                .regionName(dto.regionName)
                .build();
    }
}
