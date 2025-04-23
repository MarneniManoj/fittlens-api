package com.fittlens.core.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import javax.crypto.SecretKey;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long expiration;
    private String header;

    @Bean
    public SecretKey secretKey() {
        return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    }
} 