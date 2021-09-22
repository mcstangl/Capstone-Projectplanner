package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.enums.ProjectStatus;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest extends SpringBootTests {

    @Autowired
    private ProjectRepository projectRepository;




    @Test
    @Transactional
    @DisplayName("Find by title should return found project")
    public void findByTitle() {
        // Given
        createTestProject();

        // When
        Optional<ProjectEntity> actualOptional = projectRepository.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get().getTitle(), is("Test"));
    }

    @Test
    @Transactional
    @DisplayName("Find by title should return an empty optional if project is not in DB")
    public void findByUnknownTitle() {
        // When
        Optional<ProjectEntity> actualOptional = projectRepository.findByTitle("Unknown");

        // Then
        assertTrue(actualOptional.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("Create new project should return saved project")
    public void createNewProject() {
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title("Test2")
                .status(ProjectStatus.OPEN)
                .customer("Test2")
                .dateOfReceipt(Date.valueOf("2021-09-13"))
                .build();
        // When
        ProjectEntity actual = projectRepository.save(projectEntity);

        // Then
        assertNotNull(actual.getId());
        assertThat(actual.getStatus(), is(ProjectStatus.OPEN));
        assertThat(actual.getTitle(), is("Test2"));
        assertThat(actual.getCustomer(), is("Test2"));
        assertThat(actual.getDateOfReceipt().toString(), is("2021-09-13"));
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("getArgumentsForInvalidProjectEntityTest")
    @DisplayName("Create a project with a title that already exists or an null title should fail")
    public void createProjectInvalidTitle(String title) {
        //Given
        createTestProject();

        ProjectEntity projectEntity = ProjectEntity.builder()
                .title(title)
                .dateOfReceipt(Date.valueOf("2021-03-13"))
                .customer("Test").build();

        // Then
        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.saveAndFlush(projectEntity));
    }

    private static Stream<Arguments> getArgumentsForInvalidProjectEntityTest(){
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of("Test")
        );
    }

    @Test
    @Transactional
    @DisplayName("Create a project without a date of receipt should fail")
    public void createProjectWithoutDateOfReceipt(){
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title("New Title")
                .customer("Test").build();
        // When
        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.saveAndFlush(projectEntity));
    }

    @Test
    @Transactional
    @DisplayName("FindAll should return a list of all projects in DB")
    public void findAll() {
        // Given
        createTestProject();

        // When
        List<ProjectEntity> actual = projectRepository.findAll();

        // Then
        assertThat(actual.size(), is(1));

    }

    @Test
    @Transactional
    @DisplayName("Delete should delete project from DB")
    public void delete() {
        // Given
        createTestProject();
        Optional<ProjectEntity> fetchedProjectEntityOpt = projectRepository.findByTitle("Test");
        if (fetchedProjectEntityOpt.isEmpty()) {
            fail();
        }

        // When
        projectRepository.delete(fetchedProjectEntityOpt.get());

        // Then
        List<ProjectEntity> actual = projectRepository.findAll();
        assertThat(actual.size(), is(0));
    }


    private ProjectEntity createTestProject() {
        return projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .title("Test")
                        .dateOfReceipt(Date.valueOf("2021-09-13"))
                        .customer("Test").build()
        );
    }
}