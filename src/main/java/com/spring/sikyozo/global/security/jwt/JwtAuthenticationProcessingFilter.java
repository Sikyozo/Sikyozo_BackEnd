package com.spring.sikyozo.global.security.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.spring.sikyozo.domain.user.entity.User;
import com.spring.sikyozo.domain.user.exception.UserNotFoundException;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.exception.ErrorCodeDetail;
import com.spring.sikyozo.global.exception.domainErrorCode.AuthErrorCode;
import com.spring.sikyozo.global.redis.RedisDao;
import com.spring.sikyozo.global.security.jwt.exception.JwtExpiredException;
import com.spring.sikyozo.global.security.jwt.exception.RefreshTokenNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 인증 필터 및 Token 재발급")
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    // 아래 url로 들어오는 요청은 Filter 작동 X
    private static final String[] NO_CHECK_URLS = {
            "/login",
            "/api/users/signup",
    };

    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /*
        NO_CHECK_URLS로 요청이 들어오면, filterChain.doFilter()로 현재 필터 통과
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException, JwtExpiredException {
        log.info("JWT Filter 진입");
        log.info("requestURI: " + request.getRequestURI());
        for (String url : NO_CHECK_URLS) {
            if (request.getRequestURI().equals(url)) {
                log.info(url + " 필터 통과");
                filterChain.doFilter(request, response); // 필터 통과
                return; // 필터 작업 중단
            }
        }
        log.info("NO_CHECK_URL PASS");

        /*
            1. 요청 헤더에서 RefreshToken 추출
            2. 요청 헤더의 RefreshToken이 유효하고, Redis에 있는 RefreshToken과 일치하면 AccessToken 재발급
               - 요청 헤더에 RefreshToken이 있는 경우에는, AccessToken이 만료되어 요청한 경우뿐
        */
        try {
            String refreshTokenFromHeader = jwtProvider.extractRefreshToken(request).orElse(null);
            if (refreshTokenFromHeader != null) {
                log.info("RefreshToken 검증 시작");
                // Redis에 있는 RefreshToken에서 username 추출
                String username = jwtProvider.extractUsernameFromToken(refreshTokenFromHeader);
                log.info("JWT에서 추출한 username: {}", username);

                // 추출한 username에 해당하는 value값과 refreshTokenFromHeader를 비교해서 예외 발생
                if (redisDao.getValues(username).equals(refreshTokenFromHeader))
                    throw new RefreshTokenNotFoundException();

                jwtProvider.validateToken(refreshTokenFromHeader);
                log.info("Refresh Token 검증 완료. 정보: {}", refreshTokenFromHeader);

                reissueAccessToken(response, username);

                return; // AccessToken을 재발급하면 인증처리 하지않도록 return으로 필터 진행 막음
            }
            checkAccessTokenAndAuthentication(request, response, filterChain);
        } catch (TokenExpiredException e) {
            log.error("유효기간이 만료된 토큰입니다. : {}", e.getMessage());
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(AuthErrorCode.JWT_EXPIRED.getStatus().value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(ErrorCodeDetail.from(AuthErrorCode.JWT_EXPIRED).getMessage());
        }
    }

    // RefreshToken 검증 후 AccessToken 재발급 후 헤더로 보냄
    public void reissueAccessToken(HttpServletResponse response, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        log.info("Access Token 재발급 시작");
        String reissuedAccessToken = jwtProvider.createAccessToken(username, user.getRole().name());
        jwtProvider.sendAccessToken(response, reissuedAccessToken);
    }

    // AccessToken이 넘어왔을 때
    public void checkAccessTokenAndAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException, JwtExpiredException {
        log.info("checkAccessTokenAndAuthentication() 시작");
        String accessTokenFromHeader = jwtProvider.extractAccessToken(request).orElse(null);
        jwtProvider.validateToken(accessTokenFromHeader);

        String username = jwtProvider.extractUsernameFromToken(accessTokenFromHeader);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        saveAuthentication(user);
        log.info("Authentication 객체에 대한 인증 허가 처리 완료");

        filterChain.doFilter(request, response);
    }

    /*
        인증 허가 메서드
        SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
        setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(User currentUser) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(currentUser.getUsername())
                .password(currentUser.getPassword())
                .roles(currentUser.getRole().name())
                .build();

        // UsernamePasswordAuthenticationToken으로, Authentication 객체 생성
        // 두번째 파라미터(credential)는 보통 비밀번호로, 인증 시에는 보통 null로 제거
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("saveAuthentication() 종료");
    }

    /*
        RefreshToken 재발급 해주는 메서드
        RT 만료 시에는 에러 메시지를 발송하여 클라이언트를 로그아웃 처리하므로, 사용 안함.
        즉, RefreshToken 재발급 자체를 안함.
     */
    private String reIssueRefreshToken(String username) {
        String reIssuedRefreshToken = jwtProvider.createRefreshToken(username);
        redisDao.setValues(username, reIssuedRefreshToken);

        return reIssuedRefreshToken;
    }
}