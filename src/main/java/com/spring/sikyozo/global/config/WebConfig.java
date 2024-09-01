package com.spring.sikyozo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private CustomPageableArgumentResolver pageableArgumentResolver;

    public WebConfig(CustomPageableArgumentResolver pageableArgumentResolver) {
        this.pageableArgumentResolver = pageableArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableArgumentResolver);
    }
}
