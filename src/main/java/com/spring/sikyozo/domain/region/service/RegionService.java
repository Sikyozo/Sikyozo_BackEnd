package com.spring.sikyozo.domain.region.service;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.industry.exception.IndustryNotFoundException;
import com.spring.sikyozo.domain.region.dto.request.RegionRequestDto;
import com.spring.sikyozo.domain.region.dto.response.RegionResponseDto;
import com.spring.sikyozo.domain.region.entity.Region;
import com.spring.sikyozo.domain.region.exception.DuplicateRegionNameException;
import com.spring.sikyozo.domain.region.exception.RegionNotFoundException;
import com.spring.sikyozo.domain.region.repository.RegionRepository;
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
public class RegionService {
    private final RegionRepository regionRepository;
    private final SecurityUtil securityUtil;

    // 지역 생성
    public RegionResponseDto createRegion(RegionRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        // 지역 중복 확인
        checkRegionDuplicatioin(dto.getRegionName());

        Region region = RegionRequestDto.toEntity(dto);
        region.createdBy(currentUser);

        return RegionResponseDto.fromEntity(regionRepository.save(region));
    }

    // 지역 전체 조회
    public Page<RegionResponseDto> findAllRegions(int page, int size, String search, String sortBy, String sortDirection) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        // 페이지 크기 제한
        if (size != 10 && size != 30 && size != 50)
            size = 10; // 기본 페이지 크기로 고정

        // 정렬 방향 설정
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 검색어가 있으면 regionName을 기준으로 검색
        Page<Region> regions;

        if (search != null && !search.isEmpty())
            regions = regionRepository.findByRegionNameContainingAndDeletedAtIsNull(search, pageable);
        else regions = regionRepository.findAllByDeletedAtIsNull(pageable);

        return regions.map(RegionResponseDto::fromEntity);
    }

    // 지역 수정
    public RegionResponseDto updateRegion(UUID id, RegionRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        Region region = regionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(RegionNotFoundException::new);

        // 지역 중복 확인 및 업종 업데이트
        if (dto.getRegionName() != null) {
            checkRegionDuplicatioin(dto.getRegionName());
            region.updateRegion(dto.getRegionName());
            region.updatedBy(currentUser);
        }

        regionRepository.save(region);

        return RegionResponseDto.fromEntity(region);
    }

    // 지역 삭제 (Soft Delete)
    public MessageResponseDto deleteRegion(UUID id) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        Region region = regionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(RegionNotFoundException::new);

        region.deleteRegion();
        region.deletedBy(currentUser);
        regionRepository.save(region);

        return new MessageResponseDto("지역이 성공적으로 삭제되었습니다.");
    }

    // MANAGER, MASTER 권한 체크
    private boolean isAdmin(User user) {
        return user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.MASTER);
    }
    
    // 지역 중복 확인
    private void checkRegionDuplicatioin(String regionName) {
        Optional<Region> checkRegionName = regionRepository.findByRegionName(regionName);
        if (checkRegionName.isPresent())
            throw new DuplicateRegionNameException();
    }
}
