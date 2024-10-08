package com.spring.sikyozo.domain.store.service;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.industry.exception.EmptyIndustryListException;
import com.spring.sikyozo.domain.industry.exception.IndustryNotFoundException;
import com.spring.sikyozo.domain.industry.repository.IndustryRepository;
import com.spring.sikyozo.domain.region.entity.Region;
import com.spring.sikyozo.domain.region.repository.RegionRepository;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.entity.dto.request.CreateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.request.UpdateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.response.SearchStoreResponseDto;
import com.spring.sikyozo.domain.store.entity.dto.response.StoreResponseDto;
import com.spring.sikyozo.domain.store.entity.dto.response.UpdateStoreResponseDto;
import com.spring.sikyozo.domain.store.exception.StoreNotFoundException;
import com.spring.sikyozo.domain.store.exception.StorePermissionException;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import com.spring.sikyozo.domain.storeindustry.repository.StoreIndustryRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final IndustryRepository industryRepository;
    private final StoreIndustryRepository storeIndustryRepository;
    private final SecurityUtil securityUtil;

    // 가게 생성
    public StoreResponseDto createStore(CreateStoreRequestDto requestDto) {
        User user = isMember();

        validateOwnerRole(user);

        Region region = regionRepository.findByRegionName(requestDto.getRegionName()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 지역입니다.")
        );

        // 업종 데이터가 null이거나 비어 있는지 확인
        if (requestDto.getIndustryNames() == null || requestDto.getIndustryNames().isEmpty()) {
            throw new EmptyIndustryListException();
        }

        // 존재하는 업종인지 확인
        List<Industry> industryNames = industryRepository.findByIndustryNameIn(requestDto.getIndustryNames());
        if (industryNames.isEmpty()) {
            throw new IndustryNotFoundException();
        }

        // 존재하지 않는 업종 이름 필터링
        List<String> missingIndustries = requestDto.getIndustryNames().stream()
                .filter(name -> industryNames.stream().noneMatch(industry -> industry.getIndustryName().equals(name)))
                .collect(Collectors.toList());

        if (!missingIndustries.isEmpty()) {
            throw new IndustryNotFoundException();
        }

        // 가게 생성 및 저장
        Store store = new Store();
        store.createStore(requestDto, user, region);

        storeRepository.save(store);

        // StoreIndustry 리스트 생성
        List<StoreIndustry> storeIndustries = new ArrayList<>();

        industryNames.forEach(industry -> {
            StoreIndustry storeIndustry = new StoreIndustry();
            storeIndustry.createStoreIndustry(user, store, industry);
            storeIndustryRepository.save(storeIndustry);
            storeIndustries.add(storeIndustry);
        });

        // 가게에 업종 리스트 설정
        store.setStoreIndustries(storeIndustries);

        // 최종적으로 가게 저장
        storeRepository.save(store);

        return new StoreResponseDto(store);
    }

    // 사용자 권한 OWNER인지 확인
    private void validateOwnerRole(User user) {
        if (!UserRole.OWNER.equals(user.getRole())) {
            throw new StorePermissionException();
        }
    }

    // 가게 수정
    @Transactional
    public UpdateStoreResponseDto updateStore(UUID storeId, UpdateStoreRequestDto requestDto) {

        User user = isMember();

       validateCustomerOrManagerOrMasterRole(user);

        // 지역 확인
        Region region = regionRepository.findByRegionName(requestDto.getRegionName()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 지역입니다.")
        );

        // 가게 존재 여부 확인
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        // 업종 유무 확인
        List<StoreIndustry> existingIndustries = storeIndustryRepository.findActiveByStore(store);
        Set<String> newIndustryNameSet = new HashSet<>(requestDto.getIndustryNames());

        // 새로운 업종 목록에 없는 기존 업종 delete 설정
        existingIndustries.forEach(storeIndustry -> {
            String currentIndustryName = storeIndustry.getIndustry().getIndustryName();
            if (!newIndustryNameSet.contains(currentIndustryName)) {
                storeIndustry.deleteStoreIndustry(user);
            }
        });

        // 변경된 업종에 대한 업데이트 및 저장
        storeIndustryRepository.saveAll(existingIndustries);

        // 새 업종 추가
        List<Industry> newIndustries = industryRepository.findByIndustryNameIn(requestDto.getIndustryNames());
        newIndustries.forEach(industry -> {
            if (existingIndustries.stream().noneMatch(si -> si.getIndustry().equals(industry) && si.getDeletedAt() == null)) {
                StoreIndustry storeIndustry = new StoreIndustry();
                storeIndustry.createStoreIndustry(user, store, industry);
                storeIndustry.setDeletedAt(null);
                storeIndustryRepository.save(storeIndustry);
            }
        });

        store.updateStore(requestDto, user, region, newIndustries);
        storeRepository.save(store);

        return new UpdateStoreResponseDto(store, newIndustries);
    }

    // 가게 삭제
    @Transactional
    public void deleteStore(UUID storeId) {
        // 유저확인 & 가게주인 회원인지 확인
        User user = isMember();

       validateCustomerOrManagerOrMasterRole(user);

        // 가게 존재 여부 확인
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        store.deleteStore(user);

        // 가게와 관련된 업종 소프트 딜리트 처리
        List<StoreIndustry> storeIndustryList = storeIndustryRepository.findActiveByStore(store);

        storeIndustryList.forEach(StoreIndustry -> {
            StoreIndustry.deleteStoreIndustry(user);
            storeIndustryRepository.save(StoreIndustry);
        });

        storeRepository.save(store);
    }

    // 가게 목록 조회 (검색)
    @Transactional(readOnly = true)
    public SearchStoreResponseDto searchStores(String menuName, String industryName, Pageable pageable) {

        User user = isMember();

        validateUserRoleForAccess(user);

        Page<Store> storeList = storeRepository.findByMenuNameAndIndustryName(menuName, industryName,pageable);

        return new  SearchStoreResponseDto(storeList);
    }

    // 사용자가 고객, 매니저, 마스터 중 하나의 역할을 가지고 있는지 검증합니다.
    private void validateUserRoleForAccess(User user) {
        if (!UserRole.CUSTOMER.equals(user.getRole()) &&
                !UserRole.MANAGER.equals(user.getRole()) &&
                !UserRole.MASTER.equals(user.getRole())) {
            throw new StorePermissionException();
        }
    }

    // 사용자 확인
    private User isMember() {
        return securityUtil.getCurrentUser();
    }

    // 고객, 매니저, 마스터 권한 확인
    private void validateCustomerOrManagerOrMasterRole(User user) {
        if (!UserRole.CUSTOMER.equals(user.getRole()) &&
                !UserRole.MANAGER.equals(user.getRole()) &&
                !UserRole.MASTER.equals(user.getRole())) {
            throw new StorePermissionException();
        }
    }

}
