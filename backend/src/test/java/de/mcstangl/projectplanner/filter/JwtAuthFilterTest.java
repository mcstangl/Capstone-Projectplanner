package de.mcstangl.projectplanner.filter;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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
    @DisplayName("Login with valid Token should return login user")
    public void loginWithValidToken(){
        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                getHttpEntity( false, false),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getLoginName(), is("Hans"));
        assertThat(response.getBody().getRole(), is("ADMIN"));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForBadTokenTest")
    @DisplayName("Login with a bad token should return HttpStatus.FORBIDDEN")
    public void loginWithWrongSignature(Boolean isExpired, Boolean isSignedWrong){
        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                getHttpEntity( isExpired, isSignedWrong),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private static Stream<Arguments> getArgumentsForBadTokenTest(){
        return Stream.of(
                Arguments.of(false, true),
                Arguments.of(true, false)
        );
    }


    private String getUrl(){
        return String.format("http://localhost:%s/api/project-planner/auth/me", port);
    }

    private HttpEntity<HttpHeaders> getHttpEntity(boolean isExpired, boolean isSignedWrong){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

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
                .setSubject("Hans")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        return new HttpEntity<>(httpHeaders);
    }
}