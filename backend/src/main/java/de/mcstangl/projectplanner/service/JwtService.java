package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    @Autowired
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String createToken(UserEntity userEntity){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userEntity.getRole());

        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(3)));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userEntity.getLoginName())
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
    }

    public Claims decodeJwtClaims(String token){
        return Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
    }
}
