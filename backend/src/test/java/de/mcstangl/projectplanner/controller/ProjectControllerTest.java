package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.Project;
import de.mcstangl.projectplanner.api.UpdateProject;
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
        Project project = Project.builder()
                .title("Test Title")
                .customer("Test Customer")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
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
        Project project = Project.builder()
                .title("Test Title")
                .customer("Test Customer")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "USER")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Creating a new project with a title that is already in DB should return HttpStatus.CONFLICT")
    public void createProjectWithTitleThatAlreadyExists() {
        // Given
        Project project = Project.builder()
                .title("Test")
                .customer("Test")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
    }

    @Test
    @DisplayName("Creating a new project with a blank title should return HttpStatus.BAD_REQUEST")
    public void createProjectWithBlankTitle() {
        // Given
        Project project = Project.builder()
                .title("")
                .customer("Test")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Creating a new project with a blank customer should return HttpStatus.BAD_REQUEST")
    public void createProjectWithBlankCustomer() {
        // Given
        Project project = Project.builder()
                .title("Test")
                .customer("")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Creating a new project with a null title should return HttpStatus.BAD_REQUEST")
    public void createProjectWithoutTitle() {
        // Given
        Project project = Project.builder()
                .title(null)
                .customer("Test")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Creating a new project with a null customer should return HttpStatus.BAD_REQUEST")
    public void createProjectWithoutCustomer() {
        // Given
        Project project = Project.builder()
                .title("Test")
                .customer(null)
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(project, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Find all should return a list of all projects in DB")
    public void findAll() {
        // When
        ResponseEntity<Project[]> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                Project[].class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(1));
        assertThat(Arrays.stream(response.getBody()).toList(), contains(Project.builder()
                .title("Test")
                .customer("Test").build()));
    }

    @Test
    @DisplayName("Find by title should return project found")
    public void findByTitle() {
        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                Project.class
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
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl() + "/Unknown",
                HttpMethod.GET,
                new HttpEntity<>(null, getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectTest")
    @DisplayName("Update Project should update all fields except title when there is no new title")
    public void updateProject(String newTitle, String expectedTitle) {
        // Given
        UpdateProject updateProject = UpdateProject.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle(newTitle)
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProject, getAuthHeader("Hans", "ADMIN")),
                Project.class);

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
    @DisplayName("Update Project should return HttpStatus.BAD_REQUEST if path variable and project title don't match")
    public void updateProjectWithNonMatchingPathVariable() {
        // Given
        UpdateProject updateProject = UpdateProject.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl() + "/DoesNotMatchTitle",
                HttpMethod.PUT,
                new HttpEntity<>(updateProject, getAuthHeader("Hans", "ADMIN")),
                Project.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Update Project should return HttpStatus.UNAUTHORIZED if user is not an admin")
    public void updateProjectAsUserShouldFail() {
        // Given
        UpdateProject updateProject = UpdateProject.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<Project> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProject, getAuthHeader("Hans", "USER")),
                Project.class);

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