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
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.domain.menu.exception.MenuNotFoundException;
import lombok.RequiredArgsConstructor;
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

    public CreateMenuResponseDto createMenu(CreateMenuRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 가게입니다")
        );

        Menu menu = new Menu();
        menu.createMenu(requestDto, user, store);

        Menu savedMenu = menuRepository.save(menu);
        return new CreateMenuResponseDto(savedMenu);
    }

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

    @Transactional
    public void deleteMenu(UUID menuId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        menu.deleteMenu(user);
        menuRepository.save(menu);
    }

    @Transactional
    public void hideMenu(UUID menuId, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        menu.hideMenu(menu);
        menuRepository.save(menu);
    }

    @Transactional
    public void unHideMenu(UUID menuId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);

        menu.unHideMenu(menu);
        menuRepository.save(menu);
    }

    public List<GetMenusResponseDto> getAllMenus() {
        List<Menu> menuList = menuRepository.findByHiddenFalse();
        return menuList.stream()
                .map(GetMenusResponseDto::new)
                .collect(Collectors.toList());
    }

    public GetMenusResponseDto getMenuById(UUID menusId) {
        Menu menu = menuRepository.findById(menusId).orElseThrow(MenuNotFoundException::new);

        if (menu.isHidden()) {
            throw new MenuHiddenException();
        }
        return new GetMenusResponseDto(menu);
    }

    public List<GetMenusListResponseDto> getMenuList(String menuName, String storeName) {
        List<Menu> menuList =
                menuRepository.findByStore_StoreNameContainingAndMenuNameContainingAndHiddenFalse(storeName, menuName);
        System.out.println("menuList.size() = " + menuList.size());
        return menuList.stream()
                .map(GetMenusListResponseDto::new)
                .collect(Collectors.toList());
    }
}
