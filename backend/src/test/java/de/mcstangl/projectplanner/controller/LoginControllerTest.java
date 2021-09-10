package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.AccessTokenDto;
import de.mcstangl.projectplanner.api.CredentialsDto;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Stream;

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
                .id(1L)
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("ADMIN").build();

        UserEntity user = UserEntity.builder()
                .id(2L)
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
    @DisplayName("Login with valid CredentialsDto should return a jwt token")
    public void loginWithCredentials() {
        // Given
        CredentialsDto credentialsDto = getCredentialsDto("Hans", "password");
        HttpEntity<CredentialsDto> httpEntity = new HttpEntity<>(credentialsDto);

        // When
        ResponseEntity<AccessTokenDto> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessTokenDto.class);


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

    @ParameterizedTest
    @MethodSource("getArgumentsForInvalidCredentialsTest")
    @DisplayName("Login with wrong password should return HttpStatus.UNAUTHORIZED")
    public void loginWithInvalidPassword(String loginName, String password) {
        // Given
        CredentialsDto credentialsDto = getCredentialsDto(loginName,password);
        HttpEntity<CredentialsDto> httpEntity = new HttpEntity<>(credentialsDto);

        // When
        ResponseEntity<AccessTokenDto> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessTokenDto.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    private static Stream<Arguments> getArgumentsForInvalidCredentialsTest(){
        return Stream.of(
                Arguments.of("Hans", "invalidPassword"),
                Arguments.of("Does-not-exist", "password")
        );
    }


    @Test
    @DisplayName("Login with no loginName should return HttpStatus.BAD_REQUEST")
    public void loginWithBadCredentialsRequestLoginNameIsNull() {
        // Given
        CredentialsDto credentialsDto = getCredentialsDto(null, "password");
        HttpEntity<CredentialsDto> httpEntity = new HttpEntity<>(credentialsDto);

        // When
        ResponseEntity<AccessTokenDto> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessTokenDto.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Login with no password should return HttpStatus.BAD_REQUEST")
    public void loginWithBadCredentialsRequestPasswordIsNull() {
        // Given
        CredentialsDto credentialsDto = getCredentialsDto("Hans", null);
        HttpEntity<CredentialsDto> httpEntity = new HttpEntity<>(credentialsDto);

        // When
        ResponseEntity<AccessTokenDto> response = testRestTemplate.exchange(
                getUrl() + "/access_token",
                HttpMethod.POST,
                httpEntity,
                AccessTokenDto.class);


        //Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private CredentialsDto getCredentialsDto(String loginName, String password){
        return CredentialsDto.builder()
                .loginName(loginName)
                .password(password)
                .build();
    }

    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/auth", port);
    }
}