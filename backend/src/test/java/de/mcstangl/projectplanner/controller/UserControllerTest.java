package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.api.UserWithPasswordDto;
import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
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
    public void clear() {
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
    public void createNewUser() {
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
    public void createWithInvalidData(String loginName, String role) {
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

    public static Stream<Arguments> getArgumentsForCreateUserWithInvalidDataTest() {
        return Stream.of(
                Arguments.of("Test", null),
                Arguments.of("Test", ""),
                Arguments.of(null, "Test"),
                Arguments.of("", "Test")
        );
    }

    @Test
    @DisplayName("Create a new user as non admin user should return HttpStatus.UNAUTHORIZED")
    public void createUserAsUser() {
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

    @Test
    @DisplayName("Find user by login name should return the user found in DB")
    public void findByLoginName() {
        // Given
        UserEntity testUser = createUser();
        String loginName = testUser.getLoginName();

        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl() + "/" + loginName,
                HttpMethod.GET, new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getLoginName(), is(loginName));
    }

    @Test
    @DisplayName("Find user by login name should return HttpStatus.NOT_FOUND if the user is not in DB")
    public void findByUnknownLoginName() {
        // Given
        String loginName = "Unknown";

        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl() + loginName,
                HttpMethod.GET, new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Update user should return the updated user")
    public void updateUser() {
        // Given
        UserEntity testAdminUser = createAdminUser();
        String userToUpdateLoginName = testAdminUser.getLoginName();

        UserDto userUpdateDataDto = UserDto.builder()
                .role("USER")
                .loginName("New Name")
                .build();

        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl() + "/" + userToUpdateLoginName,
                HttpMethod.PUT,
                new HttpEntity<>(userUpdateDataDto, testUtil.getAuthHeader("ADMIN")),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getLoginName(), is("New Name"));
        assertThat(response.getBody().getRole(), is("USER"));
    }

    @Test
    @DisplayName("Update user should return HttpStatus.UNAUTHORIZED when a user wants to update an other user")
    public void aUserCanOnlyUpdateHimself() {
        // Given
        UserEntity testUser = createUser();
        String userToUpdateLoginName = testUser.getLoginName();

        UserDto userUpdateDataDto = UserDto.builder()
                .role("USER")
                .loginName("New Name")
                .build();

        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl() + "/" + userToUpdateLoginName,
                HttpMethod.PUT,
                new HttpEntity<>(userUpdateDataDto, testUtil.getAuthHeader("USER")),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateUserWithInvalidLoginNameTest")
    @DisplayName("Update user should fail when newName is blank or a user with this name already exists or the user is not found in DB")
    public void updateUserWithInvalidLoginName(String loginName, String newName, HttpStatus httpStatus) {
        // Given
        createUser();
        UserEntity testUser = userRepository.saveAndFlush(UserEntity.builder()
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.ADMIN).build());


        UserDto userUpdateDataDto = UserDto.builder()
                .role("USER")
                .loginName(newName)
                .build();

        // When
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                getUrl() + "/" + loginName,
                HttpMethod.PUT,
                new HttpEntity<>(userUpdateDataDto, testUtil.getAuthHeader("ADMIN")),
                UserDto.class);

        // Then
        assertThat(response.getStatusCode(), is(httpStatus));
    }

    private static Stream<Arguments> getArgumentsForUpdateUserWithInvalidLoginNameTest() {
        return Stream.of(
                Arguments.of("Hans", null, HttpStatus.BAD_REQUEST),
                Arguments.of("Hans", "Dave",HttpStatus.CONFLICT),
                Arguments.of("Unknown", "New Name",HttpStatus.NOT_FOUND)
        );
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