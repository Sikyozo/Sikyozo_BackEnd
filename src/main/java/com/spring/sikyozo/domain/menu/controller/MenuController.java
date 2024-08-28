package com.spring.sikyozo.domain.menu.controller;

import com.spring.sikyozo.domain.menu.entity.dto.request.CreateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.request.UpdateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.CreateMenuResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusListResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.UpdateMenuResponseDto;
import com.spring.sikyozo.domain.menu.service.MenuService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    // 상품 생성
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<CreateMenuResponseDto>> createMenu(
            @RequestBody CreateMenuRequestDto requestDto,
            @RequestParam Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus",
                        menuService.createMenu(requestDto, userId)
                ));
    }

    // 상품 수정
    @PutMapping
    public ResponseEntity<ApiSuccessResponse<UpdateMenuResponseDto>> updateMenu(
            @RequestBody UpdateMenuRequestDto requestDto,
            @RequestParam UUID menusId,
            @RequestParam Long userId) {
        menuService.updateMenu(requestDto, menusId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus?menusId=" + menusId + "&userId=" + userId,
                        menuService.updateMenu(requestDto, menusId, userId)
                ));
    }

    // 상품 삭제
    @PutMapping("/delete/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> deleteMenu(@PathVariable UUID menusId,
                                                            @RequestParam Long userId) {
        menuService.deleteMenu(menusId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "delete/" + userId,
                        "삭제에 성공했습니다."
                ));
    }

    // 상품 숨김
    @PutMapping("/hide/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> hideMenu(@PathVariable UUID menusId,
                                                          @RequestParam Long userId) {
        menuService.hideMenu(menusId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus/hide/" + menusId,
                        "상품 숨김 처리가 되었습니다."
                ));
    }

    // 상품 숨김 해제
    @PutMapping("/unhide/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> unHideMenu(@PathVariable UUID menusId,
                                                            @RequestParam Long userId) {
        menuService.unHideMenu(menusId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus/unhide/" + menusId,
                        "상품 숨김 처리 해제가 되었습니다."
                ));
    }

    // 상품 전체 조회
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<GetMenusResponseDto>>> getAllMenus() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus",
                        menuService.getAllMenus()
                ));
    }

    // 상품 단일 조회
    @GetMapping("/{menusId}")
    public ResponseEntity<ApiSuccessResponse<GetMenusResponseDto>> getMenuById(@PathVariable UUID menusId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/menus/" + menusId,
                        menuService.getMenuById(menusId)
                ));
    }

    // 상품 목록 조회
    @GetMapping("/search")
    public ResponseEntity<ApiSuccessResponse<List<GetMenusListResponseDto>>> getMenuList(@RequestParam String menuName,
                                                                                         @RequestParam String storeName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        "api/search?menuName=" + menuName + "&storeName=" + storeName,
                        menuService.getMenuList(menuName, storeName)
                ));
    }
}
