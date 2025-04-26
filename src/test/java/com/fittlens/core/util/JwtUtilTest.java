package com.fittlens.core.util;

import com.fittlens.core.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtConfig jwtConfig;
    private SecretKey secretKey;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtConfig = mock(JwtConfig.class);
        when(jwtConfig.getExpiration()).thenReturn(3600L); // 1 hour expiration
        
        secretKey = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        jwtUtil = new JwtUtil(jwtConfig, secretKey);
        
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldExtractCorrectUsername() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // Act
        boolean isValid = jwtUtil.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        // Create a token with expiration set to 1 second ago
        when(jwtConfig.getExpiration()).thenReturn(-1L);
        String expiredToken = jwtUtil.generateToken(userDetails);
        when(jwtConfig.getExpiration()).thenReturn(3600L); // Reset to normal expiration

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        Date expiration = jwtUtil.extractExpiration(token);

        // Assert
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractAllClaims_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtUtil.extractAllClaims(invalidToken));
    }

    @Test
    void createToken_WithCustomClaims_ShouldIncludeAllClaims() {
        // Arrange
        String customClaimKey = "customClaim";
        String customClaimValue = "customValue";
        Map<String, Object> claims = new HashMap<>();
        claims.put(customClaimKey, customClaimValue);

        // Act
        String token = jwtUtil.createToken(claims, userDetails.getUsername());
        Claims extractedClaims = jwtUtil.extractAllClaims(token);

        // Assert
        assertEquals(customClaimValue, extractedClaims.get(customClaimKey));
    }
} 