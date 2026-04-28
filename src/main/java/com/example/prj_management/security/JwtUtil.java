package com.example.prj_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.kafka.common.security.oauthbearer.JwtValidatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expiration = expiration;
    }

    public String generateToken(String email){
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token){
        return parseClaims(token).getSubject();
    }

    public Date getExpiration(String token){
        return parseClaims(token).getExpiration();
    }
    public boolean isTokenValid(String token){
        try{
            parseClaims(token);
            return true;
        }catch (JwtValidatorException e){
            return false;
        }
    }
    //verify token+return payload
    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                //extract to header,payload,signature+recal header
                .parseSignedClaims(token)
                .getPayload();
    }

}
