package com.spring.sikyozo.domain.address.service;

import com.spring.sikyozo.domain.address.dto.request.AddressRequestDto;
import com.spring.sikyozo.domain.address.dto.response.AddressResponseDto;
import com.spring.sikyozo.domain.address.dto.response.MessageResponseDto;
import com.spring.sikyozo.domain.address.entity.Address;
import com.spring.sikyozo.domain.address.exception.AddressNotFoundException;
import com.spring.sikyozo.domain.address.exception.DuplicateAddressNameException;
import com.spring.sikyozo.domain.address.repository.AddressRepository;
import com.spring.sikyozo.domain.user.dto.response.UserResponseDto;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.AccessDeniedException;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // 배송지 추가
    public AddressResponseDto createAddress(AddressRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        if (!isAdmin(currentUser) && !isCustomer(currentUser)) {
            throw new AccessDeniedException();
        }

        // 배송지 중복 확인
        checkAddressDuplication(dto.getAddressName());

        Address address = AddressRequestDto.toEntity(dto, currentUser);
        return AddressResponseDto.fromEntity(addressRepository.save(address));
    }

    // 모든 사용자 배송지 전체 조회 (MANAGER, MASTER)
    public Page<AddressResponseDto> findAllAddresses(int page, int size, String search, String sortBy, String sortDirection) {
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

        // 검색어가 있으면 addressName을 기준으로 검색
        Page<Address> addresses;

        if (search != null && !search.isEmpty())
            addresses = addressRepository.findByAddressNameContainingAndDeletedAtIsNull(search, pageable);
        else addresses = addressRepository.findAllByDeletedAtIsNull(pageable);

        return addresses.map(AddressResponseDto::fromEntity);
    }

    // CUSTOMER 별 배송지 전체 조회 (CUSTOMER, MANAGER, MASTER)
    public List<AddressResponseDto> findAllAddressesByUserId(Long userId) {
        User currentUser = securityUtil.getCurrentUser();

        if (!currentUser.getId().equals(userId) && !isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        User targetUser = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Address> addresses = addressRepository.findAllByUserIdAndDeletedAtIsNull(targetUser.getId());

        return addresses.stream()
                .map(AddressResponseDto::fromEntity)
                .toList();
    }

    // 배송지 수정 (CUSTOMER, MANAGER, MASTER)
    public AddressResponseDto updateAddressesByUserId(Long userId, UUID id, AddressRequestDto dto) {
        User currentUser = securityUtil.getCurrentUser();

        User targetUser = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!currentUser.getId().equals(targetUser.getId()) && !isAdmin(currentUser))
            throw new AccessDeniedException();

        Address address = addressRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(AddressNotFoundException::new);

        if (!address.getUser().getId().equals(targetUser.getId()) && !isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        boolean isUpdated = false;

        // 배송지 중복 확인
        if (dto.getAddressName() != null && !dto.getAddressName().equals(address.getAddressName())) {
            checkAddressDuplication(dto.getAddressName());
            address.updateAddress(dto.getAddressName());
            isUpdated = true;
        }

        if (dto.getRequest() != null) {
            address.updateRequest(dto.getRequest());
            isUpdated = true;
        }

        // 변경 사항이 있을 경우 updatedBy 호출
        if (isUpdated) {
            address.updatedBy(currentUser);
        }

        return AddressResponseDto.fromEntity(addressRepository.save(address));
    }

    // 배송지 삭제 (CUSTOMER, MANAGER, MASTER)
    public MessageResponseDto deleteAddressByUserId(Long userId, UUID id) {
        User currentUser = securityUtil.getCurrentUser();

        User targetUser = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!currentUser.getId().equals(targetUser.getId()) && !isAdmin(currentUser))
            throw new AccessDeniedException();

        Address address = addressRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(AddressNotFoundException::new);

        if (!address.getUser().getId().equals(targetUser.getId()) && !isAdmin(currentUser)) {
            throw new AccessDeniedException();
        }

        address.deleteAddress();
        address.deletedBy(currentUser);
        addressRepository.save(address);

        return new MessageResponseDto("배송지가 성공적으로 삭제되었습니다.");
    }

    // MANAGER, MASTER 권한 체크
    private boolean isAdmin(User user) {
        return user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.MASTER);
    }

    // CUSTOMER 권한 체크
    private boolean isCustomer(User user) {
        return user.getRole().equals(UserRole.CUSTOMER);
    }

    // 배송지 중복 확인
    private void checkAddressDuplication(String addressName) {
        // 사용자가 Soft Delete로 삭제한 주소를 복구하는 방법보단, 새로 만드는 방식이 좋을 것 같아서 DeletedAtIsNull 추가
        Optional<Address> checkAddressName = addressRepository.findByAddressNameAndDeletedAtIsNull(addressName);
        if (checkAddressName.isPresent())
            throw new DuplicateAddressNameException();
    }
}
