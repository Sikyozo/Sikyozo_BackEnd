package com.spring.sikyozo.domain.menu.controller;

import com.spring.sikyozo.domain.menu.entity.dto.request.CreateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.request.UpdateMenuRequestDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.CreateMenuResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusListResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.GetMenusResponseDto;
import com.spring.sikyozo.domain.menu.entity.dto.response.UpdateMenuResponseDto;
import com.spring.sikyozo.domain.menu.service.MenuService;
import com.spring.sikyozo.global.exception.dto.ApiSuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
            HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        menuService.createMenu(requestDto)
                ));
    }

    // 상품 수정
    @PutMapping
    public ResponseEntity<ApiSuccessResponse<UpdateMenuResponseDto>> updateMenu(
            @RequestBody UpdateMenuRequestDto requestDto,
            @RequestParam UUID menusId,
            HttpServletRequest servletRequest) {
        menuService.updateMenu(requestDto, menusId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        menuService.updateMenu(requestDto, menusId)
                ));
    }

    // 상품 삭제
    @PutMapping("/delete/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> deleteMenu(@PathVariable UUID menusId,
                                                            HttpServletRequest servletRequest) {
        menuService.deleteMenu(menusId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        "삭제에 성공했습니다."
                ));
    }

    // 상품 숨김
    @PutMapping("/hide/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> hideMenu(@PathVariable UUID menusId,
                                                          HttpServletRequest servletRequest) {
        menuService.hideMenu(menusId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        "상품 숨김 처리가 되었습니다."
                ));
    }

    // 상품 숨김 해제
    @PutMapping("/unhide/{menusId}")
    public ResponseEntity<ApiSuccessResponse<?>> unHideMenu(@PathVariable UUID menusId,
                                                            HttpServletRequest servletRequest) {
        menuService.unHideMenu(menusId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        "상품 숨김 처리 해제가 되었습니다."
                ));
    }

    // 상품 전체 조회
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<GetMenusResponseDto>>> getAllMenus(HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        menuService.getAllMenus()
                ));
    }

    // 상품 단일 조회
    @GetMapping("/{menusId}")
    public ResponseEntity<ApiSuccessResponse<GetMenusResponseDto>> getMenuById(@PathVariable UUID menusId,
                                                                               HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        menuService.getMenuById(menusId)
                ));
    }

    // 상품 목록 조회 (검색)
    @GetMapping("/search")
    public ResponseEntity<ApiSuccessResponse<Page<GetMenusListResponseDto>>> getMenuList(
            @RequestParam String menuName,
            @RequestParam String storeName,
            @RequestParam(required = false) Integer size,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest servletRequest) {

        // 페이지 크기 제한: 10, 30, 50 이외의 값은 10으로 설정
        int validatedSize = (size != null && (size == 10 || size == 30 || size == 50)) ? size : 10;

        // 새로운 Pageable 객체 생성
        Pageable validatedPageable = PageRequest.of(pageable.getPageNumber(), validatedSize, pageable.getSort());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        servletRequest.getServletPath(),
                        menuService.getMenuList(menuName, storeName, validatedPageable)
                ));
    }

}
