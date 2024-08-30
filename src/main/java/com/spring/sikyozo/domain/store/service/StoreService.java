package com.spring.sikyozo.domain.store.service;

import com.spring.sikyozo.domain.industry.entity.Industry;
import com.spring.sikyozo.domain.industry.repository.IndustryRepository;
import com.spring.sikyozo.domain.region.entity.Region;
import com.spring.sikyozo.domain.region.repository.RegionRepository;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.entity.dto.request.CreateStoreRequestDto;
import com.spring.sikyozo.domain.store.entity.dto.response.StoreResponseDto;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final IndustryRepository industryRepository;

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

        Store store = new Store();
        store.createStore(requestDto, user, region);

        storeRepository.save(store);

        List<Industry> industries = requestDto.getIndustryNames().stream()
                .map(name -> {
                    // 업종을 조회
                    Industry industry = industryRepository.findByIndustryName(name)
                            .orElseThrow(() -> new IllegalArgumentException("관련된 업종은 없습니다."));

                    // 업종 객체를 생성하고 Store를 설정
                    return new Industry(industry.getIndustryName(), store);
                })
                .collect(Collectors.toList());

        industryRepository.saveAll(industries);

        store.setIndustries(industries);

        return new StoreResponseDto(store);
    }
}
