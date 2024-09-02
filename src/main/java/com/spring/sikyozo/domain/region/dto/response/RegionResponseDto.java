package com.spring.sikyozo.domain.region.dto.response;

import com.spring.sikyozo.domain.region.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponseDto {
    private UUID id;
    private String regionName;

    public static RegionResponseDto fromEntity(Region entity) {
        return RegionResponseDto.builder()
                .id(entity.getId())
                .regionName(entity.getRegionName())
                .build();
    }
}
