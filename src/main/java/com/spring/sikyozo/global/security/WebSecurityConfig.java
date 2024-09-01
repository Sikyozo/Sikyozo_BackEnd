package com.spring.sikyozo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.sikyozo.domain.user.repository.UserRepository;
import com.spring.sikyozo.global.redis.RedisDao;
import com.spring.sikyozo.global.security.jwt.JwtAuthenticationProcessingFilter;
import com.spring.sikyozo.global.security.jwt.JwtProvider;
import com.spring.sikyozo.global.security.jwt.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.spring.sikyozo.global.security.jwt.login.handler.LoginFailureHandler;
import com.spring.sikyozo.global.security.jwt.login.handler.LoginSuccessHandler;
import com.spring.sikyozo.global.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private static final String[] USER_API_URL = {
            "/api/users",
            "/api/users/**"
    };

    private static final String[] INDUSTRY_API_URL = {
            "/api/industries",
            "/api/industries/**"
    };

    private static final String[] STORE_API_URL = {
            "/api/stores",
            "/api/stores/**"
    };

    private static final String[] MENU_API_URL = {
            "/api/menus",
            "/api/menus/**"
    };

    private static final String[] PAYMENT_API_URL = {
            "/api/payments",
            "/api/payments/**"
    };

    private static final String[] ORDER_API_URL = {
            "/api/orders",
            "/api/orders/**"
    };

    private static final String[] CART_API_URL = {
            "/api/carts",
            "/api/carts/**"
    };

    private final LoginService loginService;
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 리소스
                        .requestMatchers(USER_API_URL).permitAll()
                        .requestMatchers(INDUSTRY_API_URL).permitAll()
                        .requestMatchers(STORE_API_URL).permitAll()
                        .requestMatchers(MENU_API_URL).permitAll()
                        .requestMatchers(PAYMENT_API_URL).authenticated()
                        .requestMatchers(ORDER_API_URL).authenticated()
                        .requestMatchers(CART_API_URL).authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Custom Filter (UsernamePasswordAuthenticationFilter 실행 전에 jwtAuthFilter를 실행)
                // addFilterAfter(A,B): B필터 이후에 A 필터가 동작하도록 하는 메서드
                .addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                // addFilterBefore(A,B): B필터 이전에 A필터가 동작하도록 하는 메서드
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtProvider, redisDao);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter
                = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtProvider, redisDao, userRepository);
    }
}
