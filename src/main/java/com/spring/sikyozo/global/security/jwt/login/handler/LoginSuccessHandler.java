package com.spring.sikyozo.global.security.jwt.login.handler;

import com.spring.sikyozo.global.redis.RedisDao;
import com.spring.sikyozo.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

// JsonUsernamePasswordAuthenticationFilter 필터를 통과하여 로그인 인증 성공이 될 때 동작
@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;

    @Value("${jwt.access.expiration}")
    private String accessExpirationTime;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.info("onAuthenticationSuccess() 진입");
        String username = extractUsername(authentication);
        String role = extractRole(authentication);
        String accessToken = jwtProvider.createAccessToken(username, role);
        String refreshToken = jwtProvider.createRefreshToken();

        jwtProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // redis에 refreshToken 저장
        redisDao.setValues(username, refreshToken);

        log.info("로그인 성공. ID : {}", username);
        log.info("AccessToken : {}", accessToken);
        log.info("AccessToken 만료 기간 : {}", accessExpirationTime);
    }

    private String extractUsername(Authentication authentication) {
        log.info("extractUsername() 진입");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private String extractRole(Authentication authentication) {
        log.info("extractRole() 진입");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role not found"))
                .getAuthority();
    }
}