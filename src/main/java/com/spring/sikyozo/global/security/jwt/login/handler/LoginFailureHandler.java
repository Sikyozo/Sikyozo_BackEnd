package com.spring.sikyozo.global.security.jwt.login.handler;

import com.spring.sikyozo.global.exception.domainErrorCode.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

// JsonUsernamePasswordAuthenticationFilter 필터를 통과하여 로그인 인증 실패가 될 때 동작
@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(AuthErrorCode.LOGIN_FAILED.getMessage());
        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}