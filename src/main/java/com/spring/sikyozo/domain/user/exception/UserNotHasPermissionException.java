package com.spring.sikyozo.domain.user.exception;

public class UserNotHasPermissionException extends RuntimeException{
    public UserNotHasPermissionException(String message) {
        super(message);
    }
}
