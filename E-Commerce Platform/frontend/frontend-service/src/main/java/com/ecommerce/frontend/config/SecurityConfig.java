package com.ecommerce.frontend.config;

import com.ecommerce.frontend.client.AuthClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthClient authClient;

    public SecurityConfig(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/signup", "/products", "/products/**", "/css/**", "/js/**", "/actuator/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .addFilterBefore(new JwtCookieFilter(authClient), UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JWT_TOKEN", "USER_ID", "USER_ROLE", "USERNAME")
                .invalidateHttpSession(true)
            );

        return http.build();
    }

    static class JwtCookieFilter extends OncePerRequestFilter {

        private final AuthClient authClient;

        public JwtCookieFilter(AuthClient authClient) {
            this.authClient = authClient;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            String token = extractTokenFromCookie(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = authClient.getUserFromToken(token);
                if (user != null) {
                    var authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.get("role"))
                    );
                    var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user, null, authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);
        }

        private String extractTokenFromCookie(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
            return null;
        }
    }
}
