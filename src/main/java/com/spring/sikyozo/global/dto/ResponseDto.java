package com.spring.sikyozo.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private String status;
    private T data;
    private String message;

    public static <T> ResponseDto<T> success(String message) {
        return new ResponseDto<>("success", null, message);
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>("success", data, message);
    }

    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>("error", null, message);
    }
}
