package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import de.mcstangl.projectplanner.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.*;
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
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest extends SpringBootTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.saveAndFlush(UserEntity.builder()
                .id(1L)
                .loginName("Test")
                .password("Test")
                .role("ADMIN").build());
        userRepository.saveAndFlush(UserEntity.builder()
                .id(2L)
                .loginName("Other User")
                .password("Test")
                .role("ADMIN").build());

        projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(1L)
                        .title("Test")
                        .owner(UserEntity.builder()
                                .id(1L)
                                .loginName("Test").build())
                        .customer("Test").build()
        );
    }

    @AfterEach
    public void clear() {
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("Creating a new project should persist and return the newly created project")
    public void createNewProject() {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .title("Test Title")
                .customer("Test Customer")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is("Test Title"));
        assertThat(response.getBody().getCustomer(), is("Test Customer"));
    }

    @Test
    @DisplayName("Creating a new project as USER should return HttpStatus.UNAUTHORIZED")
    public void createNewProjectAsUserShouldFail() {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .title("Test Title")
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .customer("Test Customer")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, getAuthHeader("Hans", "USER")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Creating a new project with a title that is already in DB should return HttpStatus.CONFLICT")
    public void createProjectWithTitleThatAlreadyExists() {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .title("Test")
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .customer("Test")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForBadRequestTest")
    @DisplayName("Creating a new project with a invalid parameters should return HttpStatus.BAD_REQUEST")
    public void createProjectWithBadRequest(String title, String customer, UserDto user, HttpStatus expected) {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .title(title)
                .owner(user)
                .customer(customer)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(expected));
    }

    private static Stream<Arguments> getArgumentsForBadRequestTest() {
        UserDto userDto = UserDto.builder()
                .loginName("Test")
                .role("ADMIN").build();
        UserDto unknownUser = UserDto.builder()
                .loginName("Unknown")
                .role("ADMIN").build();
        return Stream.of(
                Arguments.of("", "Test", userDto, HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "", userDto, HttpStatus.BAD_REQUEST),
                Arguments.of("Test", null,userDto, HttpStatus.BAD_REQUEST),
                Arguments.of(null, "Test", userDto, HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", null, HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", unknownUser,HttpStatus.NOT_FOUND)
        );
    }

    @Test
    @DisplayName("Find all should return a list of all projects in DB")
    public void findAll() {
        // When
        ResponseEntity<ProjectDto[]> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                ProjectDto[].class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(1));
        assertThat(Arrays.stream(response.getBody()).toList(), contains(ProjectDto.builder()
                .title("Test")
                .customer("Test").build()));
    }

    @Test
    @DisplayName("Find by title should return project found")
    public void findByTitle() {
        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is("Test"));
    }

    @Test
    @DisplayName("Find by title should return HttpStatus.NOT_FOUND if project is not in DB")
    public void findByUnknownTitle() {
        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown",
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectTest")
    @DisplayName("Update Project should update all fields except title when there is no new title")
    public void updateProject(String newTitle, String expectedTitle) {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(UserDto.builder().loginName("Other User").role("ADMIN").build())
                .customer("New Customer")
                .title("Test")
                .newTitle(newTitle)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is(expectedTitle));
        assertThat(response.getBody().getOwner().getLoginName(), is("Other User"));
        assertThat(response.getBody().getCustomer(), is("New Customer"));
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectTest() {
        return Stream.of(
                Arguments.of(null, "Test"),
                Arguments.of("Test", "Test"),
                Arguments.of("New Title", "New Title")
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForWritersOfProjectTest")
    @DisplayName("Update Project should update the list of writers")
    public void updateWritersOfProject(List<UserDto> writers) {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(UserDto.builder().loginName("Other User").role("ADMIN").build())
                .customer("New Customer")
                .title("Test")
                .newTitle(null)
                .writer(writers)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getWriter());
        assertThat(response.getBody().getWriter().size(), is(2));
    }

    private static Stream<Arguments> getArgumentsForWritersOfProjectTest() {
        UserDto firstWriter = UserDto.builder()
                .loginName("Test").build();
        UserDto otherWriter =  UserDto.builder()
                .loginName("Other User").build();
        List<UserDto> writersToAdd = List.of(firstWriter, otherWriter);

        return Stream.of(
                Arguments.of(writersToAdd)
        );
    }

    @Test
    @DisplayName("Update Project should return HttpStatus.BAD_REQUEST if path variable and project title don't match")
    public void updateProjectWithNonMatchingPathVariable() {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/DoesNotMatchTitle",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectWithoutOwnerTest")
    @DisplayName("Update Project without an owner should return HttpStatus.BAD_REQUEST")
    public void updateProjectWithoutOwner(UserDto owner) {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(owner)
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/DoesNotMatchTitle",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, getAuthHeader("Hans", "ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectWithoutOwnerTest() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(UserDto.builder()
                        .loginName("Unknown")
                        .role("ADMIN")
                        .build())
                );
    }


    @Test
    @DisplayName("Update Project should return HttpStatus.UNAUTHORIZED if user is not an admin")
    public void updateProjectAsUserShouldFail() {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, getAuthHeader("Hans", "USER")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }


    private HttpHeaders getAuthHeader(String name, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(jwtConfig.getExpiresAfterDays())));

        String token = Jwts.builder()
                .setSubject(name)
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

    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/project", port);
    }
}