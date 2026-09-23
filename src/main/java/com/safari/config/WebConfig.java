package com.safari.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${safari.upload.dir:uploads}")
    private String uploadDir;

    private final SessionAuthInterceptor sessionAuthInterceptor;

    public WebConfig(SessionAuthInterceptor sessionAuthInterceptor) {
        this.sessionAuthInterceptor = sessionAuthInterceptor;
    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthInterceptor)
                .addPathPatterns(
                        "/bookings/**",
                        "/profile/**",
                        "/admin/**",
                        "/allocation/**",
                        "/conservation/**",
                        "/inventory/**",
                        "/finance/**",
                        "/packages/manage/**",
                        "/packages/create/**",
                        "/packages/edit/**",
                        "/packages/delete/**"
                )
                .excludePathPatterns(
                        "/packages",
                        "/packages/browse",
                        "/packages/*",
                        "/bookings/confirmation/*",
                        "/uploads/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/login",
                        "/register",
                        "/logout",
                        "/demo/**",
                        "/error"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
