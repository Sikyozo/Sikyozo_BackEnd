package com.spring.sikyozo.domain.menu.service;

import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.menu.entity.dto.request.CreateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.request.UpdateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.CreateMenuResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusListResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.UpdateMenuResponseDto;
import com.spring.sikyozo.domain.menu.exception.MenuHiddenException;
import com.spring.sikyozo.domain.menu.repository.MenuRepository;
import com.spring.sikyozo.domain.store.entity.Store;
import com.spring.sikyozo.domain.store.repository.StoreRepository;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.entity.UserRole;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
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

    // 상품 생성
    public CreateMenuResponseDto createMenu(CreateMenuRequestDto requestDto, Long userId) {
        // 로그인 유저 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        // 유저가 가게 주인 회원인지 확인
        if (!UserRole.OWNER.equals(user.getRole())) {
            throw new IllegalArgumentException("가게 주인 회원이 아닙니다.");
        }

        // 가게 유무 확인
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 가게입니다")
        );

        Menu menu = new Menu();
        menu.createMenu(requestDto, user, store);

        Menu savedMenu = menuRepository.save(menu);
        return new CreateMenuResponseDto(savedMenu);
    }

    // 상품 수정
    @Transactional
    public UpdateMenuResponseDto updateMenu(UpdateMenuRequestDto requestDto, UUID menusId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menusId).orElseThrow(MenuNotFoundException::new);

        menu.updateMenu(requestDto, user);

        Menu savedMenu = menuRepository.save(menu);
        return new UpdateMenuResponseDto(savedMenu);
    }

    // 상품 삭제
    @Transactional
    public void deleteMenu(UUID menuId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        menu.deleteMenu(user);
        menuRepository.save(menu);
    }

    // 상품 숨김
    @Transactional
    public void hideMenu(UUID menuId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        menu.hideMenu(menu);
        menuRepository.save(menu);
    }

    // 상품 숨김 해제
    @Transactional
    public void unHideMenu(UUID menuId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

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
        Menu menu = menuRepository.findById(menusId).orElseThrow(MenuNotFoundException::new);

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
}
