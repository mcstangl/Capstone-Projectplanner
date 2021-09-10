package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    @BeforeEach
    public void setup() {
        projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(1L)
                        .title("Test")
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
    @DisplayName("Creating a new project with a blank title should return HttpStatus.BAD_REQUEST")
    public void createProjectWithBadRequest(String title, String customer) {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .title(title)
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
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private static Stream<Arguments> getArgumentsForBadRequestTest(){
        return Stream.of(
                Arguments.of("", "Test"),
                Arguments.of("Test", ""),
                Arguments.of("Test", null),
                Arguments.of(null, "Test")
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
    @DisplayName("Update ProjectDto should update all fields except title when there is no new title")
    public void updateProject(String newTitle, String expectedTitle) {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
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
        assertNotNull(response.getBody().getTitle(),expectedTitle );
        assertThat(response.getBody().getCustomer(), is("New Customer"));
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectTest() {
        return Stream.of(
                Arguments.of( null, "Test"),
                Arguments.of("Test", "Test"),
                Arguments.of("New Title", "New Title")
        );
    }

    @Test
    @DisplayName("Update ProjectDto should return HttpStatus.BAD_REQUEST if path variable and project title don't match")
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

    @Test
    @DisplayName("Update ProjectDto should return HttpStatus.UNAUTHORIZED if user is not an admin")
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