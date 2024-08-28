package com.spring.sikyozo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.sikyozo.global.redis.RedisDao;
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

    private final LoginService loginService;
    private final JwtProvider jwtProvider;
    private final RedisDao redisDao;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/").permitAll()
                                .requestMatchers(USER_API_URL).permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()  // 정적 리소스
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Custom Filter (UsernamePasswordAuthenticationFilter 실행 전에 jwtAuthFilter를 실행)
                // addFilterAfter(A,B): B필터 이후에 A 필터가 동작하도록 하는 메서드
                .addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
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
}
