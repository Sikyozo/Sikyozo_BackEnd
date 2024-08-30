package com.spring.sikyozo.domain.industry.dto.response;

import com.spring.sikyozo.domain.industry.entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndustryResponseDto {
    private UUID id;
    private String industryName;

    public static IndustryResponseDto fromEntity(Industry entity) {
        return IndustryResponseDto.builder()
                .id(entity.getId())
                .industryName(entity.getIndustryName())
                .build();
    }
}
