package com.spring.sikyozo.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

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
    public String createRefreshToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        return JWT.create()
                .withSubject(username)
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

    /*
        AccessToken이 재발급 될 때 사용되는 메서드
        형식: { "Authorization" : {token} }
     */
    public void sendAccessToken(
            HttpServletResponse response,
            String accessToken
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Map<String, String> token = new HashMap<>();
        token.put(accessHeader, accessToken);

        // 헤더와 accessToken이 담긴 Map 형태의 데이터를 String으로 변환
        String result = objectMapper.writeValueAsString(token);
        log.info("Map 형태의 accessToken 정보를 String으로 변환: " + result);

        // 클라이언트에게 문자 형태로 응답을 하기 위함
        response.getWriter().write(result);

        log.info("재발급된 Access Token: {}", accessToken);
    }

    // AccessToken 헤더에 담긴 "Bearer "제거
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        log.info("extractAccessToken() 시작");
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    // RefreshToken 헤더에 담긴 "Bearer "제거
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        log.info("extractRefreshToken() 시작");
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    // (Redis에서) username 조회하는 메서드
    public String extractUsernameFromToken(String token) {
        // JWT 토큰의 서명을 검증하고, Payload에서 Claims 추출
        DecodedJWT decodedJWT = validateToken(token);

        // JWT의 Subject 필드에 저장된 값 (username 또는 userId)을 추출하여 반환
        return decodedJWT.getSubject();  // JWT의 "sub" 필드를 통해 username을 가져옴
    }

    /*
        JWT(AccessToken, RefreshToken) 유효성 검증
        -> 서버에서 발급된 유효한 토큰인지, 유효기간이 만료되지 않았는지, 변조되지 않았는지 등을 검사
        헤더의 토큰을 HS512 인증 방식을 통해 디코딩 후 유효성 확인
     */
    public DecodedJWT validateToken(String token) {
        log.info("토큰 유효성 검증 validateToken() 시작");
        // JWT 검증 및 DecodedJWT 반환
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);  // 검증 실패 시 예외 발생
    }
}
