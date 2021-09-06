package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {


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
                .signWith(SignatureAlgorithm.HS256, "a very secret secret")
                .compact();
    }
}
