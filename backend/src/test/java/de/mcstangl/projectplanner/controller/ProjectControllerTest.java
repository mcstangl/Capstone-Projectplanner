package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.Project;
import de.mcstangl.projectplanner.config.JwtConfig;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import de.mcstangl.projectplanner.service.ProjectService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.*;
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
    public void setup(){
        projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .title("Test")
                        .customer("Test").build()
        );
    }

    @AfterEach
    public void clear(){
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is("Test Title"));
        assertThat(response.getBody().getCustomer(), is("Test Customer"));
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
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
                new HttpEntity<>(project,getAuthHeader("Hans", "ADMIN")),
                Project.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    private HttpHeaders getAuthHeader(String name, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Date iat = Date.from(Instant.now());
        Date exp = Date.from(Instant.now().plus(Duration.ofDays(1)));

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