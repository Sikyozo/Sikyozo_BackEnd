package com.spring.sikyozo.domain.industry.service;

import com.spring.sikyozo.domain.industry.dto.request.IndustryRequestDto;
import com.spring.sikyozo.domain.industry.dto.response.IndustryResponseDto;
import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.industry.exception.DuplicateIndustryNameException;
import com.spring.sikyozo.domain.industry.exception.IndustryNotFoundException;
import com.spring.sikyozo.domain.industry.repository.IndustryRepository;
import com.spring.sikyozo.domain.user.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.AccessDeniedException;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class IndustryService {
    private final IndustryRepository industryRepository;
    private final SecurityUtil securityUtil;

    // 업종 생성
    public IndustryResponseDto createIndustry(IndustryRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        // 업종 중복 확인
        checkIndustryDuplicatioin(dto.getIndustryName());

        Industry industry = IndustryRequestDto.toEntity(dto);
        industry.createdBy(currentUser);

        return IndustryResponseDto.fromEntity(industryRepository.save(industry));
    }

    // 업종 전체 조회
    public Page<IndustryResponseDto> findAllIndustries(int page, int size, String search, String sortBy, String sortDirection) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser) && !isOwner(currentUser)) {
            throw new AccessDeniedException();
        }

        // 페이지 크기 제한
        if (size != 10 && size != 30 && size != 50)
            size = 10; // 기본 페이지 크기로 고정

        // 정렬 방향 설정
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 검색어가 있으면 industryName을 기준으로 검색
        Page<Industry> industries;

        if (search != null && !search.isEmpty())
            industries = industryRepository.findByIndustryNameContainingAndDeletedAtIsNull(search, pageable);
        else industries = industryRepository.findAllByDeletedAtIsNull(pageable);

        return industries.map(IndustryResponseDto::fromEntity);
    }

    // 업종 수정
    public IndustryResponseDto updateIndustry(UUID id, IndustryRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        Industry industry = industryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(IndustryNotFoundException::new);

        // 업종 중복 확인 및 업종 업데이트
        if (dto.getIndustryName() != null) {
            checkIndustryDuplicatioin(dto.getIndustryName());
            industry.updateIndustry(dto.getIndustryName());
            industry.updatedBy(currentUser);
        }

        industryRepository.save(industry);

        return IndustryResponseDto.fromEntity(industry);
    }

    // 업종 삭제 (Soft Delete)
    public MessageResponseDto deleteIndustry(UUID id) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        Industry industry = industryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(IndustryNotFoundException::new);

        industry.deleteIndustry();
        industry.deletedBy(currentUser);
        industryRepository.save(industry);

        return new MessageResponseDto("업종이 성공적으로 삭제되었습니다.");
    }

    // MANAGER, MASTER 권한 체크
    private boolean isAdmin(User user) {
        return user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.MASTER);
    }

    // OWNER 권한 체크
    private boolean isOwner(User user) {
        return user.getRole().equals(UserRole.OWNER);
    }

    // 업종 중복 확인
    private void checkIndustryDuplicatioin(String industryName) {
        Optional<Industry> checkIndustryName = industryRepository.findByIndustryName(industryName);
        if (checkIndustryName.isPresent())
            throw new DuplicateIndustryNameException();
    }
}
