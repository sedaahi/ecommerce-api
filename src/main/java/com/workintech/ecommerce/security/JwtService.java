package com.workintech.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
//JwtService → token üretir, email'i çıkarır, token'ı doğrular.
//JwtAuthenticationFilter → gelen request'teki Authorization: Bearer ... token'ını yakalar ve kontrol eder.
//CustomUserDetailsService → email üzerinden DB'deki kullanıcıyı Spring Security'ye tanıtır.
@Service
public class JwtService {

    // JWT'yi imzalamak için kullanılan gizli anahtar.
    // Daha sonra application.properties / environment variable'a taşıyacağız.
    private static final String SECRET_KEY =
            "ecommerce-super-secret-jwt-key-2026-seda-project";

    // Token 24 saat geçerli olacak.
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // JWT oluşturur.
    public String generateToken(String email) {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Token içerisindeki email'i çıkarır.
    public String extractEmail(String token) {

        return extractClaims(token).getSubject();
    }

    // Token geçerli mi ve doğru kullanıcıya mı ait?
    public boolean isTokenValid(String token, String email) {

        String tokenEmail = extractEmail(token);

        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    // Token'ın süresi dolmuş mu?
    private boolean isTokenExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Token'ın içerisindeki claim'leri okur.
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // String secret key'i JWT'nin kullanabileceği SecretKey'e çevirir.
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }
}