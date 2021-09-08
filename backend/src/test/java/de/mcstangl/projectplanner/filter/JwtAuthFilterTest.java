package de.mcstangl.projectplanner.filter;

import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtAuthFilterTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtConfig jwtConfig;

    @Test
    public void loginWithValidToken(){
        // Given

    }

    private HttpEntity getHttpEntity(String name, String role, boolean isExpired){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);


        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));

        if(isExpired){
            iat = Date.from(Instant.now().minus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));
        }
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(name)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
                .compact();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        return new HttpEntity(httpHeaders);
    }
}