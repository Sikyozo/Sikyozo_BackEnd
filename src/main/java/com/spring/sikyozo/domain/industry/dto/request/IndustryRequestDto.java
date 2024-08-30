package com.spring.sikyozo.domain.industry.dto.request;

import com.spring.sikyozo.domain.industry.entity.Industry;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndustryRequestDto {
    @NotBlank(message = "업종을 입력해 주세요.")
    private String industryName;

    public static Industry toEntity(IndustryRequestDto dto) {
        return Industry.builder()
                .industryName(dto.industryName)
                .build();
    }
}
