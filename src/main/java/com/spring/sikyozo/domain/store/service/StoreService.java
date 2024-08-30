package com.spring.sikyozo.domain.store.service;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.industry.repository.IndustryRepository;
import com.spring.sikyozo.domain.region.entity.Region;
import com.spring.sikyozo.domain.region.repository.RegionRepository;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.entity.dto.request.CreateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.request.UpdateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.response.StoreResponseDto;
import com.spring.sikyozo.domain.store.entity.dto.response.UpdateStoreResponseDto;
import com.spring.sikyozo.domain.store.exception.StoreNotFoundException;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.storeindustry.entity.StoreIndustry;
import com.spring.sikyozo.domain.storeindustry.repository.StoreIndustryRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final IndustryRepository industryRepository;
    private final StoreIndustryRepository storeIndustryRepository;

    // 가게 생성
    public StoreResponseDto createStore(CreateStoreRequestDto requestDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        if (!UserRole.OWNER.equals(user.getRole())) {
            throw new IllegalArgumentException("가게 주인 회원이 아닙니다.");
        }

        Region region = regionRepository.findByRegionName(requestDto.getRegionName()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않은 지역입니다.")
        );

        // 업종 데이터가 null이거나 비어 있는지 확인
        if (requestDto.getIndustryNames() == null || requestDto.getIndustryNames().isEmpty()) {
            throw new IllegalArgumentException("업종 목록이 비어있습니다.");
        }

        // 존재하는 업종인지 확인
        List<Industry> industryNames = industryRepository.findByIndustryNameIn(requestDto.getIndustryNames());
        if (industryNames.isEmpty()) {
            throw new IllegalArgumentException("입력한 업종 이름들 중 존재하지 않는 업종이 있습니다.");
        }

        // 존재하지 않는 업종 이름 필터링
        List<String> missingIndustries = requestDto.getIndustryNames().stream()
                .filter(name -> industryNames.stream().noneMatch(industry -> industry.getIndustryName().equals(name)))
                .collect(Collectors.toList());

        if (!missingIndustries.isEmpty()) {
            throw new IllegalArgumentException("다음 업종이 존재하지 않습니다: " + missingIndustries);
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

//    // 가게 수정
//    @Transactional
//    public UpdateStoreResponseDto updateStore(UUID storeId, Long id, UpdateStoreRequestDto requestDto) {
//
//        // 유저확인 & 가게주인 회원인지 확인
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
//        );
//
//        if (!UserRole.OWNER.equals(user.getRole())) {
//            throw new IllegalArgumentException("가게 주인 회원이 아닙니다.");
//        }
//
//        // 지역 확인
//        Region region = regionRepository.findByRegionName(requestDto.getRegionName()).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않은 지역입니다.")
//        );
//
//        // 가게 존재 여부 확인
//        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
//
//        // 가게 업종 조회 (소프트 딜리트 되지 않은 것만)
//        List<Industry> existingIndustries = industryRepository.findByStoreIdAndDeletedAtIsNull(store.getId());
//
//        // 새로 입력 받은 업종 리스트
//        List<String> newIndustryNames = requestDto.getIndustryNames().stream()
//                .distinct()
//                .collect(Collectors.toList());
//
//        List<Industry> industriesToUpdate = existingIndustries.stream()
//                .filter(industry -> newIndustryNames.contains(industry.getIndustryName()))
//                .collect(Collectors.toList());
//
//        // 새로운 업종 추가
//        List<Industry> industriesToAdd = newIndustryNames.stream()
//                .filter(name -> existingIndustries.stream()
//                        .noneMatch(industry -> industry.getIndustryName().equals(name)))
//                .map(name -> new Industry(name,store,user))
//                .collect(Collectors.toList());
//
//        // 새로운 업종 저장
//        if (!industriesToAdd.isEmpty()) {
//            industryRepository.saveAll(industriesToAdd);
//        }
//
//        // 기존 업종 중에서 삭제된 업종들 삭제 처리 (소프트 딜리트)
//        List<Industry> industriesToDelete = existingIndustries.stream()
//                .filter(industry -> !newIndustryNames.contains(industry.getIndustryName()))
//                .collect(Collectors.toList());
//
//        if (!industriesToDelete.isEmpty()) {
//            industriesToDelete.forEach(industry -> {
//                industry.deleteIndustry(user); // 소프트 딜리트 처리
//                industryRepository.save(industry); // 업데이트
//            });
//        }
//
//        store.updateStore(requestDto,user,region,industriesToUpdate);
//        storeRepository.save(store);
//
//        // 업데이트된 가게와 관련된 업종 정보 포함
//        List<Industry> updatedIndustries = industryRepository.findByStoreIdAndDeletedAtIsNull(storeId);
//        return new UpdateStoreResponseDto(store,updatedIndustries);
//    }

    // 가게 삭제
//    @Transactional
//    public void deleteStore(UUID storeId, Long userId) {
//        // 유저확인 & 가게주인 회원인지 확인
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
//        );
//
//        if (!UserRole.OWNER.equals(user.getRole())) {
//            throw new IllegalArgumentException("가게 주인 회원이 아닙니다.");
//        }
//
//        // 가게 존재 여부 확인
//        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
//        store.deleteStore(user);
//
//        // 가게와 관련된 업종 소프트 딜리트 처리
//        List<Industry> industryList = industryRepository.findByStore(store);
//
//        industryList.forEach(industry ->{
//            industry.deleteIndustry(user);
//            industryRepository.save(industry);
//        });
//
//        storeRepository.save(store);
//    }

    // 가게 목록 조회 (검색)
    public void searchStores(Long userId, String menuName, String industryName) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );
    }
}
