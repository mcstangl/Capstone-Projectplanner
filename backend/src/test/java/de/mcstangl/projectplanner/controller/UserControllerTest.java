package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.api.UserWithPasswordDto;
import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import de.mcstangl.projectplanner.service.UserService;
import de.mcstangl.projectplanner.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends SpringBootTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @AfterEach
    public void clear(){
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Find all should return all users in DB")
    public void findAll() {
        // Given
        createAdminUser();
        createUser();

        // When
        ResponseEntity<UserDto[]> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                UserDto[].class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(2));
    }

    @Test
    @DisplayName("Find all should as non admin user should return HttpStatus.UNAUTHORIZED")
    public void findAllAsUser() {
        // When
        ResponseEntity<UserDto[]> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                UserDto[].class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));

    }
    @Test
    @DisplayName("Create a new user should return the user with password")
    public void createNewUser(){
        // Given
        UserDto userDto = UserDto.builder()
                .role("USER")
                .loginName("Test")
                .build();

        // When
        ResponseEntity<UserWithPasswordDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(userDto, testUtil.getAuthHeader("ADMIN")),
                UserWithPasswordDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getLoginName(), is("Test"));
        assertThat(response.getBody().getRole(), is("USER"));
        assertThat(response.getBody().getPassword().length(), is(12));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForCreateUserWithInvalidDataTest")
    @DisplayName("Create a new user without loginName or role should return HttpStatus.BAD_REQUEST")
    public void createWithInvalidData(String loginName, String role){
        // Given
        UserDto userDto = UserDto.builder()
                .role(role)
                .loginName(loginName)
                .build();

        // When
        ResponseEntity<UserWithPasswordDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(userDto, testUtil.getAuthHeader("ADMIN")),
                UserWithPasswordDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    public static Stream<Arguments> getArgumentsForCreateUserWithInvalidDataTest(){
        return Stream.of(
                Arguments.of("Test", null),
                Arguments.of("Test", ""),
                Arguments.of(null, "Test"),
                Arguments.of("", "Test")
        );
    }

    @Test
    @DisplayName("Create a new user as non admin user should return HttpStatus.UNAUTHORIZED")
    public void createUserAsUser(){
        // Given
        UserDto userDto = UserDto.builder()
                .role("USER")
                .loginName("Test")
                .build();

        // When
        ResponseEntity<UserWithPasswordDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(userDto, testUtil.getAuthHeader("USER")),
                UserWithPasswordDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }


    private UserEntity createAdminUser() {
        return userRepository.save(UserEntity.builder()
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.ADMIN).build());
    }

    private UserEntity createUser() {
        return userRepository.save(UserEntity.builder()
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.USER).build());
    }

    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/user", port);
    }
}