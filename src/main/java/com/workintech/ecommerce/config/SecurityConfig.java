package com.workintech.ecommerce.config;

import com.workintech.ecommerce.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // REST API kullandığımız için CSRF kapalı.
                .csrf(csrf -> csrf.disable())

                // Form Login ve Basic Auth kullanmıyoruz.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // JWT kullandığımız için session tutmuyoruz.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // Login gerektirmeyen endpointler.
                        .requestMatchers(
                                "/roles",
                                "/signup",
                                "/login"
                        ).permitAll()

                        // Geri kalan endpointler JWT gerektirir.
                        .anyRequest().authenticated()
                )

                // JWT filtresi Spring Security filtresinden önce çalışır.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}