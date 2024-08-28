package com.spring.sikyozo.domain.menu.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.MenuErrorCode;

public class MenuHiddenException extends SikyozoException {
    public MenuHiddenException() {
        super(MenuErrorCode.MENU_IS_HIDDEN);

    }
}
