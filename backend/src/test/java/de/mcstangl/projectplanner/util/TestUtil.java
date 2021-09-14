package de.mcstangl.projectplanner.util;

import de.mcstangl.projectplanner.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TestUtil {


    private final JwtConfig jwtConfig;

    @Autowired
    public TestUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }



    public HttpHeaders getAuthHeader(String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));

        String token = Jwts.builder()
                .setSubject("Hans")
                .setClaims(claims)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();

        HttpHeaders authHeader = new HttpHeaders();
        authHeader.setBearerAuth(token);
        authHeader.setContentType(MediaType.APPLICATION_JSON);

        return authHeader;
    }
}
