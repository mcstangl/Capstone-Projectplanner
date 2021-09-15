package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    private MilestoneEntity testMilestone1;
    private MilestoneEntity testMilestone2;
    private MilestoneEntity testMilestone3;
    private ProjectEntity testProject1;
    private ProjectEntity testProject2;

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

        testProject2 = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(2L)
                        .dateOfReceipt(Date.valueOf("2021-01-01"))
                        .title("Test2")
                        .customer("Test")
                        .build()
        );

        testMilestone1 = milestoneRepository.save(
                MilestoneEntity.builder()
                        .id(1L)
                        .projectEntity(testProject1)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
        testMilestone2 = milestoneRepository.saveAndFlush(
                MilestoneEntity.builder()
                        .id(2L)
                        .projectEntity(testProject1)
                        .dateFinished(Date.valueOf("2021-12-12"))
                        .dueDate(Date.valueOf("2021-03-13"))
                        .title("Test")
                        .build()
        );
        testMilestone3 = MilestoneEntity.builder()
                .projectEntity(testProject2)
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test3")
                .build();


    }

    @AfterEach
    public void tearDown(){
        milestoneRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Find by project should return the milestone found")
    public void findByProject() {
        // When
        List<MilestoneEntity> actual = milestoneRepository.findAllByProjectEntity(testProject1);

        // Then
        assertThat(actual.size(), is(2));
        assertThat(actual, containsInAnyOrder(testMilestone1, testMilestone2));
    }

    @Test
    @Transactional
    @DisplayName("Save should persist the milestone to DB")
    public void save() {
        // When
        MilestoneEntity actual = milestoneRepository.save(testMilestone3);

        // Then
        assertThat(actual.getTitle(), is("Test3"));
        assertNotNull(actual.getId());
        assertThat(actual.getProjectEntity().getTitle(), is("Test2"));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForSaveTest")
    @DisplayName("Save milestone without title or project should fail")
    public void saveWithoutTitleAndProject(String title, ProjectEntity project){
        // Given
        MilestoneEntity testMilestone = MilestoneEntity.builder()
                .title(title)
                .projectEntity(project)
                .build();

        // When
        assertThrows(DataIntegrityViolationException.class, ()-> milestoneRepository.save(testMilestone));
    }


private static Stream<Arguments> getArgumentsForSaveTest(){
    ProjectEntity testProject =
            ProjectEntity.builder()
                    .id(1L)
                    .dateOfReceipt(Date.valueOf("2021-01-01"))
                    .title("Test1")
                    .customer("Test")
                    .build();
        return Stream.of(
                Arguments.of(null, testProject),
                Arguments.of("Test2", null)
        );
    }
}