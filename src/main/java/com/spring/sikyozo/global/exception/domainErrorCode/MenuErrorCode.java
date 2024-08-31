package com.spring.sikyozo.global.exception.domainErrorCode;

import com.spring.sikyozo.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MenuErrorCode implements ErrorCode {
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU_NOT_FOUND","해당 메뉴를 찾을 수 없습니다."),
    MENU_IS_HIDDEN(HttpStatus.NOT_FOUND, "MENU_IS_HIDDEN", "해당 메뉴는 숨김 상태 입니다."),
    MENU_IS_DELETED(HttpStatus.BAD_REQUEST, "MENU_IS_DELETE", "삭제된 메뉴입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MenuErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
