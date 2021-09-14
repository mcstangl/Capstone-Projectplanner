package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.MileStoneRepository;
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


class MileStoneEntityTest extends SpringBootTests {


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

        testMilestone1 = mileStoneRepository.save(
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
    public void tearDown(){
        mileStoneRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Find by project should return the milestone found")
    public void findByProject() {
        // When
        List<MileStoneEntity> actual = mileStoneRepository.findAllByProjectEntity(testProject1);

        // Then
        assertThat(actual.size(), is(2));
        assertThat(actual, containsInAnyOrder(testMilestone1, testMilestone3));
    }

    @Test
    @Transactional
    @DisplayName("Save should persist the milestone to DB")
    public void save() {
        // Given
        testMilestone2.setId(null);

        // When
        MileStoneEntity actual = mileStoneRepository.save(testMilestone2);

        // Then
        assertThat(actual.getTitle(), is("Test2"));
        assertNotNull(actual.getId());
        assertThat(actual.getProjectEntity().getTitle(), is("Test2"));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForSaveTest")
    @DisplayName("Save milestone without title or project should fail")
    public void saveWithoutTitleAndProject(String title, ProjectEntity project){
        // Given
        MileStoneEntity testMilestone = MileStoneEntity.builder()
                .title(title)
                .projectEntity(project)
                .build();

        // When
        assertThrows(DataIntegrityViolationException.class, ()->mileStoneRepository.save(testMilestone));
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