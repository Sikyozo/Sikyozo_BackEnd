package com.spring.sikyozo.domain.menu.exception;

import com.spring.sikyozo.global.exception.SikyozoException;
import com.spring.sikyozo.global.exception.domainErrorCode.MenuErrorCode;

public class MenuDeletedException extends SikyozoException {
    public MenuDeletedException() {
        super(MenuErrorCode.MENU_IS_DELETED);
    }
}
