package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


class MilestoneEntityTest extends SpringBootTests {


    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectRepository projectRepository;


    @Test
    @Transactional
    @DisplayName("Find by project should return the milestone found")
    public void findByProject() {
        // Given
        ProjectEntity testProject = createTestProject();
        createTestMilestone(testProject);
        createTestMilestone(testProject);

        // When
        List<MilestoneEntity> actual = milestoneRepository.findAllByProjectEntity(testProject);

        // Then
        assertThat(actual.size(), is(2));
    }


    @Test
    @Transactional
    @DisplayName("Save should persist the milestone to DB")
    public void save() {
        // Given
        ProjectEntity testProject = createTestProject();

        // When
        MilestoneEntity actual = milestoneRepository.save(MilestoneEntity.builder()
                .projectEntity(testProject)
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test3")
                .build());

        // Then
        assertNotNull(actual.getId());
        assertThat(actual.getTitle(), is("Test3"));
        assertThat(actual.getProjectEntity().getTitle(), is("Test1"));
    }

    @Test
    @Transactional
    @DisplayName("Update a milestone should change all fields")
    public void update() {
        // Given
        ProjectEntity testProject = createTestProject();
        MilestoneEntity testMilestone = createTestMilestone(testProject);
        Long idToUpdate = testMilestone.getId();

        // When
        MilestoneEntity actual = milestoneRepository.save(MilestoneEntity.builder()
                .id(idToUpdate)
                .projectEntity(testProject)
                .dateFinished(Date.valueOf("2021-01-01"))
                .dueDate(Date.valueOf("2021-01-01"))
                .title("New Title")
                .build());

        // Then
        assertThat(actual.getTitle(), is("New Title"));
        assertThat(actual.getId(), is(idToUpdate));
        assertThat(actual.getDateFinished().toString(), is("2021-01-01"));
        assertThat(actual.getDueDate().toString(), is("2021-01-01"));
        assertThat(actual.getProjectEntity().getTitle(), is("Test1"));
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("getArgumentsForSaveTest")
    @DisplayName("Save milestone without title or project should fail")
    public void saveWithoutTitleAndProject(String title, ProjectEntity project) {
        // Given
        MilestoneEntity testMilestone = MilestoneEntity.builder()
                .title(title)
                .projectEntity(project)
                .build();

        // Then
        assertThrows(DataIntegrityViolationException.class, () -> milestoneRepository.save(testMilestone));
    }


    private static Stream<Arguments> getArgumentsForSaveTest() {
        ProjectEntity testProject =
                ProjectEntity.builder()
                        .id(1L)
                        .title("Test1")
                        .build();
        return Stream.of(
                Arguments.of(null, testProject),
                Arguments.of("New Milestone", null)
        );
    }

    private ProjectEntity createTestProject() {
        return projectRepository.save(
                ProjectEntity.builder()
                        .dateOfReceipt(Date.valueOf("2021-01-01"))
                        .title("Test1")
                        .customer("Test")
                        .build()
        );
    }

    private MilestoneEntity createTestMilestone(ProjectEntity testProject) {
        return milestoneRepository.save(
                MilestoneEntity.builder()
                        .projectEntity(testProject)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test1")
                        .build()
        );

    }


}