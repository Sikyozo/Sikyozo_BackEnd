package com.spring.sikyozo.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "JWTProvider")
@RequiredArgsConstructor
@Getter
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessExpirationTime;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationTime;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String ROLE_CLAIM = "role";
    private static final String BEARER = "Bearer ";

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    // AccessToken 생성
    public String createAccessToken(String username, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .withClaim(ROLE_CLAIM, role)
                .withClaim("token_type", ACCESS_TOKEN_SUBJECT)
                .sign(Algorithm.HMAC512(secretKey));
    }

    // RefreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .withClaim("token_type", REFRESH_TOKEN_SUBJECT)
                .sign(Algorithm.HMAC512(secretKey));
    }

    /*
        초기 로그인 성공 시 Response Body에 두 토큰의 값을 담아 반환
        형식: { "Authorization" : {AccessToken}, "AuthorizationRefresh" : {RefreshToken}  }
     */
    public void sendAccessAndRefreshToken(
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Map<String, String> token = new HashMap<>();
        token.put(accessHeader, accessToken);
        token.put(refreshHeader, refreshToken);

        String result = objectMapper.writeValueAsString(token);
        log.info("Map 형태의 AccessToken, RefreshToken 정보를 String으로 변환: " + result);

        response.getWriter().write(result);
        log.info("AccessToken, RefreshToken Body에 설정 완료");
    }
}
