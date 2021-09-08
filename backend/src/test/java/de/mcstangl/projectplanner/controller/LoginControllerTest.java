package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.AccessToken;
import de.mcstangl.projectplanner.api.Credentials;
import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginControllerTest extends SpringBootTests {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtConfig jwtConfig;

    @BeforeEach
    public void setup() {
        UserEntity admin = UserEntity.builder()
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("ADMIN").build();

        UserEntity user = UserEntity.builder()
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("USER").build();
        userRepository.saveAndFlush(admin);
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    public void clearDB() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Login with valid Credentials should return a jwt token")
    public void loginWithCredentials() {
        // Given
        Credentials credentials = Credentials.builder()
                .loginName("Hans")
                .password("password")
                .build();
        HttpEntity httpEntity = new HttpEntity(credentials);

        // When
        ResponseEntity<AccessToken> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessToken.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        try {
            String token = response.getBody().getToken();
            Claims claims = Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
            assertThat(claims.getSubject(), is("Hans"));
            assertThat(claims.get("role"), is("ADMIN"));

        } catch (JwtException e) {
            fail();
        }

    }

    @Test
    @DisplayName("Login with wrong password should return HttpStatus.UNAUTHORIZED")
    public void loginWithInvalidPassword() {
        // Given
        Credentials credentials = Credentials.builder()
                .loginName("Hans")
                .password("invalidPassword")
                .build();
        HttpEntity httpEntity = new HttpEntity(credentials);

        // When
        ResponseEntity<AccessToken> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessToken.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Login with wrong loginName should return HttpStatus.UNAUTHORIZED")
    public void loginWithInvalidLoginName() {
        // Given
        Credentials credentials = Credentials.builder()
                .loginName("Does-not-exist")
                .password("password")
                .build();
        HttpEntity httpEntity = new HttpEntity(credentials);

        // When
        ResponseEntity<AccessToken> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessToken.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Login with no loginName should return HttpStatus.BAD_REQUEST")
    public void loginWithBadCredentialsRequestLoginNameIsNull() {
        // Given
        Credentials credentials = Credentials.builder()
                .loginName(null)
                .password("password")
                .build();
        HttpEntity httpEntity = new HttpEntity(credentials);

        // When
        ResponseEntity<AccessToken> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessToken.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Login with no password should return HttpStatus.BAD_REQUEST")
    public void loginWithBadCredentialsRequestPasswordIsNull() {
        // Given
        Credentials credentials = Credentials.builder()
                .loginName("Hans")
                .password(null)
                .build();
        HttpEntity httpEntity = new HttpEntity(credentials);

        // When
        ResponseEntity<AccessToken> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessToken.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/auth", port);
    }
}