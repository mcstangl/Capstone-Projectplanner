package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("FindByName should return an optional project when the project is found")
    public void findByTitle() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        when(projectRepositoryMock.findByTitle(any())).thenReturn(
              Optional.of(ProjectEntity.builder()
                      .id(1L)
                      .customer("Test")
                      .title("Test")
                      .build())
        );
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get(), is(ProjectEntity.builder()
                .id(1L)
                .customer("Test")
                .title("Test")
                .build()));

    }

    @Test
    @DisplayName("FindByName should return an optional empty when the project is not found")
    public void findByTitleUnknown() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        when(projectRepositoryMock.findByTitle(any())).thenReturn(
                Optional.empty()
        );
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Unknown");

        // Then
        assertTrue(actualOptional.isEmpty());
    }

    @Test
    @DisplayName("FindAll should return all projects in DB")
    public void findAll() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        when(projectRepositoryMock.findAll()).thenReturn(
                List.of(ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build())
        );
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        List<ProjectEntity> actual = projectService.findAll();

        // Then
        assertThat(actual.size(), is(1));
        assertThat(actual, contains(ProjectEntity.builder()
                .id(1L)
                .customer("Test")
                .title("Test")
                .build()));
    }

    @Test
    @DisplayName("Creating a new project should return the newly created project")
    public void createNewProject() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        when(projectRepositoryMock.save(any())).thenReturn(
                ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build()
        );
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        ProjectEntity newProject = projectService.createNewProject(ProjectEntity.builder()
                .customer("Test")
                .title("Test")
                .build());

        // Then
        assertThat(newProject, is(ProjectEntity.builder()
                .id(1L)
                .customer("Test")
                .title("Test")
                .build()));

    }



    @Test
    @DisplayName("Creating a new project with a title that is already in DB should throw EntityExistsException")
    public void createNewProjectWithTitleThatAlreadyExists() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        when(projectRepositoryMock.save(any())).thenReturn(
                ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build()
        );
        when(projectRepositoryMock.findByTitle(any())).thenReturn(
                Optional.of(ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build())
        );
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        try {
            projectService.createNewProject(ProjectEntity.builder()
                    .customer("Test")
                    .title("Test")
                    .build());
            fail();
        } catch (EntityExistsException e) {
            assertThat(e.getClass(), is(EntityExistsException.class));
        }
    }


    @Test
    @DisplayName("Creating a new project with a blank title should throw IllegalArgumentException")
    public void createNewProjectWithBlankTitle() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        try {
            projectService.createNewProject(ProjectEntity.builder()
                    .customer("Test")
                    .title("")
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getClass(), is(IllegalArgumentException.class));
        }
    }

    @Test
    @DisplayName("Creating a new project with a blank customer should throw IllegalArgumentException")
    public void createNewProjectWithBlankCustomer() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        try {
            projectService.createNewProject(ProjectEntity.builder()
                    .customer("")
                    .title("Test")
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getClass(), is(IllegalArgumentException.class));
        }
    }

    @Test
    @DisplayName("Creating a new project with a null title should throw IllegalArgumentException")
    public void createNewProjectWithNullTitle() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        try {
            projectService.createNewProject(ProjectEntity.builder()
                    .customer("Test")
                    .title(null)
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getClass(), is(IllegalArgumentException.class));
        }
    }

    @Test
    @DisplayName("Creating a new project with a null customer should return throw IllegalArgumentException")
    public void createNewProjectWithNullCustomer() {
        // Given
        ProjectRepository projectRepositoryMock = mock(ProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepositoryMock);

        // When
        try {
            projectService.createNewProject(ProjectEntity.builder()
                    .customer(null)
                    .title("Test")
                    .build());
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getClass(), is(IllegalArgumentException.class));
        }
    }
}