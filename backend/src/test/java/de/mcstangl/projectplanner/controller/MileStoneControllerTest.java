package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.MileStoneDto;
import de.mcstangl.projectplanner.model.MileStoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MileStoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import de.mcstangl.projectplanner.util.TestUtil;
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
import org.springframework.http.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MileStoneControllerTest extends SpringBootTests {


    @LocalServerPort
    private int port;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MileStoneRepository mileStoneRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private MileStoneEntity testMilestone1;
    private MileStoneEntity testMilestone2;
    private MileStoneEntity testMilestone3;
    private ProjectEntity testProject1;

    @BeforeEach
    public void setup() {
        testProject1 = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(1L)
                        .dateOfReceipt(Date.valueOf("2021-01-01"))
                        .title("Test1")
                        .customer("Test")
                        .build()
        );

        ProjectEntity testProject2 = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(2L)
                        .dateOfReceipt(Date.valueOf("2021-01-01"))
                        .title("Test2")
                        .customer("Test")
                        .build()
        );

        testMilestone1 = mileStoneRepository.saveAndFlush(
                MileStoneEntity.builder()
                        .id(1L)
                        .projectEntity(testProject1)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
        testMilestone2 = mileStoneRepository.saveAndFlush(
                MileStoneEntity.builder()
                        .id(2L)
                        .projectEntity(testProject1)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
        testMilestone3 = MileStoneEntity.builder()
                .projectEntity(testProject2)
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test3")
                .build();


    }

    @AfterEach
    public void tearDown() {
        mileStoneRepository.deleteAll();
        projectRepository.deleteAll();
    }


    @Test
    @DisplayName("Find by project title should return all milestones related the project")
    public void findAllByProjectTitle() {
        // When
        ResponseEntity<MileStoneDto[]> response = testRestTemplate.exchange(
                getUrl() + "/Test1",
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                MileStoneDto[].class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(2));
    }

    @Test
    @DisplayName("Create new milestone should return the milestone created")
    public void createNewMilestone(){
        // Given
        MileStoneDto mileStoneDto = MileStoneDto.builder()
                .projectTitle("Test1")
                .title("Test")
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MileStoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MileStoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(),is("Test"));
        assertThat(response.getBody().getDateFinished(),is("2021-12-12"));
        assertThat(response.getBody().getDueDate() ,is("2021-03-13"));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForCreateNewMilestoneTest")
    @DisplayName("Create new milestone without title or unknown project title should fail")
    public void createNewMilestone(String title, String projectTitle, HttpStatus httpStatus){
        // Given
        MileStoneDto mileStoneDto = MileStoneDto.builder()
                .projectTitle(projectTitle)
                .title(title)
                .dueDate("2021-03-13")
                .dateFinished("2021-12-12")
                .build();

        // When
        ResponseEntity<MileStoneDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(mileStoneDto, testUtil.getAuthHeader("ADMIN")),
                MileStoneDto.class);

        // Then
        assertThat(response.getStatusCode(), is(httpStatus));
    }


    private static Stream<Arguments> getArgumentsForCreateNewMilestoneTest(){
        return Stream.of(
                Arguments.of(null, "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of("", "Test1", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Unknown", HttpStatus.NOT_FOUND),
                Arguments.of("Test", null , HttpStatus.NOT_FOUND)

        );
    }


    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/milestone", port);
    }

}