package com.buoi4.baoduy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()

                // Chỉ ADMIN mới được CRUD
                .requestMatchers("/products/create",
                                 "/products/save",
                                 "/products/edit/**",
                                 "/products/delete/**").hasRole("ADMIN")

                // Xem danh sách sản phẩm: cả 2 role
                .requestMatchers("/products", "/products/").hasAnyRole("USER", "ADMIN")

                // Shop
                .requestMatchers("/shop/**").hasAnyRole("USER", "ADMIN")

                // Giỏ hàng + đặt hàng: cả 2 role
                .requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/checkout/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/order-success").hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(roleBasedSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                Authentication authentication) -> {

            boolean isAdmin = authentication.getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            String redirectUrl = isAdmin ? "/products" : "/products";
            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}