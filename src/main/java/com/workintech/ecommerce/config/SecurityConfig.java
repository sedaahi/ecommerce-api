package com.workintech.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // REST API kullandığımız için CSRF kapalı.
                .csrf(csrf -> csrf.disable())

                // Form login ve Basic Auth kullanmıyoruz.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // JWT kullanacağımız için session tutmuyoruz.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // Giriş yapmadan erişilebilen endpointler.
                        .requestMatchers(
                                "/roles",
                                "/signup",
                                "/login"
                        ).permitAll()

                        // Diğer endpointler authentication gerektirir.
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}