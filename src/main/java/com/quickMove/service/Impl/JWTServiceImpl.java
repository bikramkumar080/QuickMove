package com.quickMove.service.Impl;

import com.quickMove.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {



    @Value("${google.maps.api.token}")
    public String token;
    public String generateToken(UserDetails userDetails, String role){
        return Jwts.builder()
                   .subject(userDetails.getUsername())
                   .header().empty().add("typ","JWT")
                   .and()
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                   .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolvers){
        final Claims claims=extractAllClaim(token);
        return claimResolvers.apply(claims);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(token);
        return Keys.hmacShaKeyFor(key);
    }

    public  boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUserName(token);
        return (username.equals(userDetails.getUsername())&& !isTokeExpire(token));
    }

    private boolean isTokeExpire(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }
}
