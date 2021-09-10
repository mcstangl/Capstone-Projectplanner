package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest extends SpringBootTests {

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
    @Transactional
    @DisplayName("Find by title should return found project")
    public void findByTitle() {
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
        Optional<ProjectEntity> actualOptional = projectRepository.findByTitle("Unkown");

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
                .customer("Test2").build();
        // When
        ProjectEntity actual = projectRepository.saveAndFlush(projectEntity);

        // Then
        assertNotNull(actual.getId());
        assertThat(actual.getTitle(), is("Test2"));
        assertThat(actual.getCustomer(), is("Test2"));
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("getArgumentsForInvalidProjectEntityTest")
    @DisplayName("Create a project with a title that already exists or an null title should fail")
    public void createProjectInvalidTitle(String title) {
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title(title)
                .customer("Test").build();
        // When
        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.saveAndFlush(projectEntity));
    }

    private static Stream<Arguments> getArgumentsForInvalidProjectEntityTest(){
        return Stream.of(
                Arguments.of("Test"),
                Arguments.of((Object) null)
        );
    }


    @Test
    @Transactional
    @DisplayName("FindAll should return a list of all projects in DB")
    public void findAll() {
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
}