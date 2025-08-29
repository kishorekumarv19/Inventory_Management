package com.inventory.inventory_management.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;

/**
 * Utility class for handling JWT operations.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LogManager.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        logger.debug("Generating token for username: {}", username);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        logger.debug("Generated token: {}", token);
        return token;
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        logger.debug("Extracting username from token: {}", token);
        String username = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        logger.debug("Extracted Username: {}", username);
        return username;
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("Validating token: {}", token);
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            logger.debug("Token is valid");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token", e);
            return false;
        }
    }
}