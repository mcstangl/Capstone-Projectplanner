package de.mcstangl.projectplanner.filter;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.User;
import de.mcstangl.projectplanner.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtAuthFilterTest extends SpringBootTests {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void loginWithValidToken(){
        // When
        ResponseEntity<User> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                getHttpEntity("Hans", "ADMIN", false, false),
                User.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getLoginName(), is("Hans"));
        assertThat(response.getBody().getRole(), is("ADMIN"));
    }

    @Test
    public void loginWithWrongSignature(){
        // When
        ResponseEntity<User> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                getHttpEntity("Hans", "ADMIN", false, true),
                User.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void loginWithExpiredToken(){
        // When
        ResponseEntity<User> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                getHttpEntity("Hans", "ADMIN", true, false),
                User.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    private String getUrl(){
        return String.format("http://localhost:%s/api/project-planner/auth/me", port);
    }

    private HttpEntity getHttpEntity(String name, String role, boolean isExpired, boolean isSignedWrong){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        String secret = jwtConfig.getSecret();
        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));

        if(isExpired){
            iat = Date.from(Instant.now().minus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));
            exp = Date.from(Instant.now().minus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));
        }

        if(isSignedWrong){
            secret = secret + "invalid Signature";
        }


        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(name)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        return new HttpEntity(httpHeaders);
    }
}