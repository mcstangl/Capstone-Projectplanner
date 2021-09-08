package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityExistsException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
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
}