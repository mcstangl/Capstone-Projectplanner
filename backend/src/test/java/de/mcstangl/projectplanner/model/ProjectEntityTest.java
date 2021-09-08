package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectEntityTest extends SpringBootTests {

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
    @DisplayName("Find by login name should return found project")
    public void findByLoginName(){
        // When
        Optional<ProjectEntity> actualOptional = projectRepository.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
    }

    @Test
    @DisplayName("Create new project should return saved project")
    public void createNewProject(){
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title("Test2")
                .customer("Test2").build();
        // When
        ProjectEntity actual = projectRepository.saveAndFlush(projectEntity);

        // Then
        assertThat(actual.getTitle(), is("Test2"));
        assertThat(actual.getCustomer(), is("Test2"));
    }

    @Test
    @DisplayName("Create a project with a title that already exists should fail")
    public void createProjectWithTitleThatAlreadyExists(){
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title("Test")
                .customer("Test").build();
        // When
        try{
            ProjectEntity actual = projectRepository.saveAndFlush(projectEntity);
            fail();
        }catch (DataIntegrityViolationException e){
            // Then
            assertEquals(DataIntegrityViolationException.class, e.getClass());
        }
    }

    @Test
    @DisplayName("Create a project with a null title should fail")
    public void createProjectWithBlankTitle(){
        //Given
        ProjectEntity projectEntity = ProjectEntity.builder()
                .title(null)
                .customer("Test").build();
        // When
        try{
            ProjectEntity actual = projectRepository.saveAndFlush(projectEntity);
            fail();
        }catch (DataIntegrityViolationException e){
            // Then
            assertEquals(DataIntegrityViolationException.class, e.getClass());
        }
    }

}