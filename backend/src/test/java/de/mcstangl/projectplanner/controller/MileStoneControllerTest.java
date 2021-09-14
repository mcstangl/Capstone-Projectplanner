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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.sql.Date;

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
        testMilestone2 = MileStoneEntity.builder()
                .id(2L)
                .projectEntity(testProject2)
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test2")
                .build();

        testMilestone3 = mileStoneRepository.saveAndFlush(
                MileStoneEntity.builder()
                        .id(3L)
                        .projectEntity(testProject1)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );

    }

    @AfterEach
    public void tearDown() {
        mileStoneRepository.deleteAll();
        projectRepository.deleteAll();
    }


    @Test
    @DisplayName("Find by project title should return all milestones related the project")
    public void findAllByProjectTitle() {
        // Given
        String projectTitle = "Test1";
        HttpHeaders httpHeaders = testUtil.getAuthHeader("USER");
        // When
        ResponseEntity<MileStoneDto[]> response = testRestTemplate.exchange(
                getUrl() + "/" + projectTitle,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                MileStoneDto[].class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(2));
    }


    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/milestone", port);
    }

}