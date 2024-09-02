package com.spring.sikyozo.domain.menu.service;

import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.entity.dto.request.CreateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.request.UpdateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.CreateMenuResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusListResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.UpdateMenuResponseDto;
import com.spring.sikyozo.domain.menu.exception.MenuDeletedException;
import com.spring.sikyozo.domain.menu.exception.MenuHiddenException;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import com.spring.sikyozo.domain.menu.repository.MenuRepository;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.exception.StoreNotFoundException;
import com.spring.sikyozo.domain.store.exception.StorePermissionException;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.exception.InvalidRoleException;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final SecurityUtil securityUtil;

    // 상품 생성
    public CreateMenuResponseDto createMenu(CreateMenuRequestDto requestDto) {
        // 로그인 유저 확인
        User user = getUser();

        // 유저가 가게 주인 회원인지 확인
        if (!UserRole.OWNER.equals(user.getRole())) {
            throw new InvalidRoleException();
        }

        // 가게 유무 확인
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(StoreNotFoundException::new);

        Menu menu = new Menu();
        menu.createMenu(requestDto, user, store);

        Menu savedMenu = menuRepository.save(menu);
        return new CreateMenuResponseDto(savedMenu);
    }

    // 상품 수정
    @Transactional
    public UpdateMenuResponseDto updateMenu(UpdateMenuRequestDto requestDto, UUID menusId) {
        User user = getUser();

        validateCustomerOrManagerOrMasterRole(user);

        Menu menu = getMenu(menusId);

        menu.updateMenu(requestDto, user);

        Menu savedMenu = menuRepository.save(menu);
        return new UpdateMenuResponseDto(savedMenu);
    }

    // 상품 삭제
    @Transactional
    public void deleteMenu(UUID menuId) {
        User user = getUser();

        validateCustomerOrManagerOrMasterRole(user);

        Menu menu = getMenu(menuId);

        menu.deleteMenu(user);
        menuRepository.save(menu);
    }

    // 상품 숨김
    @Transactional
    public void hideMenu(UUID menuId) {

        User user = getUser();

        validateCustomerOrManagerOrMasterRole(user);

        Menu menu = getMenu(menuId);

        // 상품이 삭제되어있는지 확인
        if (menu.getDeletedAt() != null) {
            throw new MenuDeletedException();
        }

        menu.hideMenu(menu);
        menuRepository.save(menu);
    }

    // 상품 숨김 해제
    @Transactional
    public void unHideMenu(UUID menuId) {
        User user = getUser();

        validateCustomerOrManagerOrMasterRole(user);

        Menu menu = getMenu(menuId);

        // 상품이 삭제되어있는지 확인
        if (menu.getDeletedAt() != null) {
            throw new MenuDeletedException();
        }

        menu.unHideMenu(menu);
        menuRepository.save(menu);
    }

    // 상품 전체 조회
    public List<GetMenusResponseDto> getAllMenus() {
        List<Menu> menuList = menuRepository.findByHiddenFalse();
        return menuList.stream()
                .map(GetMenusResponseDto::new)
                .collect(Collectors.toList());
    }

    // 상품 단일 조회
    public GetMenusResponseDto getMenuById(UUID menusId) {
        Menu menu = getMenu(menusId);

        // 상품 숨김 상태 확인
        if (menu.isHidden()) {
            throw new MenuHiddenException();
        }
        return new GetMenusResponseDto(menu);
    }

    // 상품 목록 조회
    public Page<GetMenusListResponseDto> getMenuList(String menuName, String storeName, Pageable pageable) {
        Page<Menu> menuPage =
                menuRepository.findByStore_StoreNameContainingAndMenuNameContainingAndHiddenFalse(storeName, menuName, pageable);
        return menuPage.map(GetMenusListResponseDto::new);
    }

    // 회원 여부 확인
    private User getUser() {
        return securityUtil.getCurrentUser();
    }

    // 메뉴 여부 확인
    private Menu getMenu(UUID menusId) {
        return menuRepository.findById(menusId).orElseThrow(MenuNotFoundException::new);
    }

    // 사용자 권한 (OWNER, MANAGER, MASTER) 확인
    private void validateCustomerOrManagerOrMasterRole(User user) {
        if (!UserRole.OWNER.equals(user.getRole()) &&
                !UserRole.MANAGER.equals(user.getRole()) &&
                !UserRole.MASTER.equals(user.getRole())) {
            throw new StorePermissionException();
        }
    }
}
