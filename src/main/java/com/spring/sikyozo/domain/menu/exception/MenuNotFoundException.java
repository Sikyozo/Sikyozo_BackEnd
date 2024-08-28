package com.spring.sikyozo.domain.menu.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.MenuErrorCode;

public class MenuNotFoundException extends SikyozoException {

    public MenuNotFoundException() {
        super(MenuErrorCode.MENU_NOT_FOUND);
    }
}
