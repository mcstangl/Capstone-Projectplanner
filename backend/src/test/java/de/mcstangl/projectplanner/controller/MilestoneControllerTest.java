package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.MilestoneDto;
import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
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
import org.springframework.http.*;

import java.sql.Date;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MilestoneControllerTest extends SpringBootTests {


    @LocalServerPort
    private int port;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectRepository projectRepository;


    @AfterEach
    public void tearDown() {
        milestoneRepository.deleteAll();
        projectRepository.deleteAll();
    }


    @Test
    @DisplayName("Find by project title should return all milestones related the project")
    public void findAllByProjectTitle() {
        // Given
        ProjectEntity testProject = createTestProject();
        createTestMilestone1(testProject);
        createTestMilestone2(testProject);

        // When
        ResponseEntity<MilestoneDto[]> response = testRestTemplate.exchange(
                getUrl() + "/Test1",
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                MilestoneDto[].class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(2));
    }

    @Test
    @DisplayName("Create new milestone should return the milestone created")
    public void createNewMilestone() {
        // Given
        createTestProject();
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .projectTitle("Test1")
                .title("New Milestone")
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertThat(response.getBody().getTitle(), is("New Milestone"));
        assertThat(response.getBody().getDateFinished(), is("2021-12-12"));
        assertThat(response.getBody().getDueDate(), is("2021-03-13"));
    }

    @Test
    @DisplayName("Create new milestone as user should fail")
    public void createNewMilestoneAsUser() {
        // Given
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .projectTitle("Test1")
                .title("Test")
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("USER")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForCreateNewMilestoneTest")
    @DisplayName("Create new milestone without title or unknown project title should fail")
    public void createNewMilestone(String title, String projectTitle, HttpStatus httpStatus) {
        // Given
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .projectTitle(projectTitle)
                .title(title)
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(httpStatus));
    }


    private static Stream<Arguments> getArgumentsForCreateNewMilestoneTest() {
        return Stream.of(
                Arguments.of(null, "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of("", "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Unknown", HttpStatus.NOT_FOUND),
                Arguments.of("Test", null, HttpStatus.NOT_FOUND)

        );
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForCreateNewMilestoneWithBadDatesTest")
    @DisplayName("Create new milestone with a bad date should set the date to null")
    public void createNewMilestoneWithBadDates(String dueDate, String dateFinished, String expectedDueDate, String expectedDateFinished) {
        // Given
        createTestProject();

        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .projectTitle("Test1")
                .title("New Milestone")
                .dueDate(dueDate)
                .dateFinished(dateFinished)
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getDueDate(), is(expectedDueDate));
        assertThat(response.getBody().getDateFinished(), is(expectedDateFinished));

    }


    private static Stream<Arguments> getArgumentsForCreateNewMilestoneWithBadDatesTest() {
        return Stream.of(
                Arguments.of(null, "2001-12-12", null, "2001-12-12"),
                Arguments.of("2001-12-12", null, "2001-12-12", null),
                Arguments.of("noDate", "2001-12-12", null, "2001-12-12"),
                Arguments.of("2001-12-12", "noDate", "2001-12-12", null)
        );
    }

    @Test
    @DisplayName("Update milestone should return the milestone created")
    public void updateMilestone() {
        // Given
        ProjectEntity testProject = createTestProject();
        MilestoneEntity testMilestone1 = createTestMilestone1(testProject);
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .id(testMilestone1.getId())
                .projectTitle("Test1")
                .title("Updated Milestone")
                .dueDate("2021-01-01")
                .dateFinished("2021-01-01")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getId(), is(testMilestone1.getId()));
        assertThat(response.getBody().getProjectTitle(), is("Test1"));
        assertThat(response.getBody().getTitle(), is("Updated Milestone"));
        assertThat(response.getBody().getDateFinished(), is("2021-01-01"));
        assertThat(response.getBody().getDueDate(), is("2021-01-01"));
    }

    @Test
    @DisplayName("Update new milestone as user should fail")
    public void updateMilestoneAsUser() {
        // Given
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .id(1L)
                .projectTitle("Test1")
                .title("Test")
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("USER")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateNewMilestoneTest")
    @DisplayName("Update milestone without id or title or unknown project title should fail")
    public void updateMilestoneWithoutTitle(Long id, String title, String projectTitle, HttpStatus httpStatus) {
        // Given
        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .id(id)
                .projectTitle(projectTitle)
                .title(title)
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(httpStatus));
    }


    private static Stream<Arguments> getArgumentsForUpdateNewMilestoneTest() {
        return Stream.of(
                Arguments.of(1L, null, "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of(null, "Test", "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of(1L, "", "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of(1L, "Test", "Unknown", HttpStatus.NOT_FOUND),
                Arguments.of(1L, "Test", null, HttpStatus.NOT_FOUND)

        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateMilestoneWithBadDatesTest")
    @DisplayName("Update milestone with a bad date should set the date to null")
    public void updateMilestoneWithBadDates(String dueDate, String dateFinished, String expectedDueDate, String expectedDateFinished) {
        // Given
        createTestProject();

        MilestoneDto mileStoneDto = MilestoneDto.builder()
                .id(1L)
                .projectTitle("Test1")
                .title("New Milestone")
                .dueDate(dueDate)
                .dateFinished(dateFinished)
                .build();

        // When
        ResponseEntity<MilestoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MilestoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getDueDate(), is(expectedDueDate));
        assertThat(response.getBody().getDateFinished(), is(expectedDateFinished));

    }


    private static Stream<Arguments> getArgumentsForUpdateMilestoneWithBadDatesTest() {
        return Stream.of(
                Arguments.of(null, "2001-12-12", null, "2001-12-12"),
                Arguments.of("2001-12-12", null, "2001-12-12", null),
                Arguments.of("noDate", "2001-12-12", null, "2001-12-12"),
                Arguments.of("2001-12-12", "noDate", "2001-12-12", null)
        );
    }

    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/milestone", port);
    }


    public ProjectEntity createTestProject() {
        return projectRepository.save(
                ProjectEntity.builder()
                        .id(1L)
                        .dateOfReceipt(Date.valueOf("2021-01-01"))
                        .title("Test1")
                        .customer("Test")
                        .build()
        );
    }

    public MilestoneEntity createTestMilestone1(ProjectEntity testProject) {
        return milestoneRepository.save(
                MilestoneEntity.builder()
                        .id(1L)
                        .projectEntity(testProject)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
    }

    public void createTestMilestone2(ProjectEntity testProject) {
        milestoneRepository.save(
                MilestoneEntity.builder()
                        .id(2L)
                        .projectEntity(testProject)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
    }
}