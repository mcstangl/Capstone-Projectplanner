package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    private AutoCloseable closeable;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("FindByName should return an optional project when the project is found")
    public void findByTitle() {
        // Given
        when(projectRepository.findByTitle(any())).thenReturn(
                Optional.of(ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build())
        );


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
        when(projectRepository.findByTitle(any())).thenReturn(
                Optional.empty()
        );

        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Unknown");

        // Then
        assertTrue(actualOptional.isEmpty());
    }

    @Test
    @DisplayName("FindAll should return all projects in DB")
    public void findAll() {
        // Given
        when(projectRepository.findAll()).thenReturn(
                List.of(ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build())
        );

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
        when(projectRepository.save(any())).thenReturn(
                ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build()
        );

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
        when(projectRepository.save(any())).thenReturn(
                ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build()
        );
        when(projectRepository.findByTitle(any())).thenReturn(
                Optional.of(ProjectEntity.builder()
                        .id(1L)
                        .customer("Test")
                        .title("Test")
                        .build())
        );

        // When
        assertThrows(EntityExistsException.class, () -> projectService.createNewProject(ProjectEntity.builder()
                .customer("Test")
                .title("Test")
                .build()));
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForInvalidProjectTest")
    @DisplayName("Creating a new project with an invalid project data should throw IllegalArgumentException")
    public void createNewProjectInvalidProjectData(String title, String customer) {
        // When
        assertThrows(IllegalArgumentException.class, () -> projectService.createNewProject(ProjectEntity.builder()
                .customer(customer)
                .title(title)
                .build()));
    }

    private static Stream<Arguments> getArgumentsForInvalidProjectTest() {
        return Stream.of(
                Arguments.of("Test", ""),
                Arguments.of("", "Test"),
                Arguments.of(null, "Test"),
                Arguments.of("Test", null)
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectTest")
    @DisplayName("Updating all project fields except title")
    public void updateProject(String newTitle) {
        // Given
        when(projectRepository.findByTitle(any()))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(UserEntity.builder()
                                        .id(1L)
                                        .loginName("Test")
                                        .role("ADMIN")
                                        .build())
                                .customer("Test")
                                .title("Test")
                                .build()));

        when(projectRepository.save(any()))
                .thenReturn(
                        ProjectEntity.builder()
                                .owner(UserEntity.builder()
                                        .id(2L)
                                        .loginName("New User")
                                        .role("ADMIN")
                                        .build())
                                .id(1L)
                                .customer("New Customer")
                                .title("Test")
                                .build());

        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(UserEntity.builder()
                        .id(2L)
                        .loginName("New User")
                        .role("ADMIN")
                        .build())
                .customer("New Customer")
                .title("Test")
                .build();
        // When
        ProjectEntity actual = projectService.update(projectEntity, newTitle);

        // Then
        assertThat(actual.getTitle(), is("Test"));
        assertThat(actual.getCustomer(), is("New Customer"));
        assertNotNull(actual.getOwner());
        assertThat(actual.getOwner().getLoginName(), is("New User"));
        assertNotNull(actual.getId());
        verify(projectRepository, times(1)).save(ProjectEntity.builder()
                .id(1L)
                .owner(UserEntity.builder()
                        .id(2L)
                        .loginName("New User")
                        .role("ADMIN")
                        .build())
                .title("Test")
                .build());
        verify(projectRepository, times(1)).findByTitle("Test");
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectTest() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of("Test")
        );
    }

    @Test
    @DisplayName("Updating all project fields")
    public void updateAllFieldsProject() {
        // Given
        when(projectRepository.findByTitle("Test"))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(UserEntity.builder()
                                        .id(1L)
                                        .loginName("Test")
                                        .role("ADMIN")
                                        .build())
                                .customer("Test")
                                .title("Test")
                                .build()))
                .thenReturn(Optional.empty());

        when(projectRepository.save(any()))
                .thenReturn(
                        ProjectEntity.builder()
                                .id(1L)
                                .owner(UserEntity.builder()
                                        .id(2L)
                                        .loginName("New User")
                                        .role("ADMIN")
                                        .build())
                                .customer("New Customer")
                                .title("new Title")
                                .build());

        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(UserEntity.builder()
                        .id(2L)
                        .loginName("New User")
                        .role("ADMIN")
                        .build())
                .customer("New Customer")
                .title("Test")
                .build();
        // When
        ProjectEntity actual = projectService.update(projectEntity, "new Title");

        // Then
        assertThat(actual.getTitle(), is("new Title"));
        assertThat(actual.getCustomer(), is("New Customer"));
        assertNotNull(actual.getOwner());
        assertThat(actual.getOwner().getLoginName(), is("New User"));
        assertNotNull(actual.getId());
        verify(projectRepository, times(1)).save(
                ProjectEntity.builder()
                        .owner(UserEntity.builder()
                                .id(2L)
                                .loginName("New User")
                                .role("ADMIN")
                                .build())
                        .title("new Title")
                        .customer("New Customer")
                        .build());
        verify(projectRepository, times(1)).delete(ProjectEntity.builder()
                .id(1L)

                .title("Test")
                .build());
        verify(projectRepository, times(1)).findByTitle("Test");
        verify(projectRepository, times(1)).findByTitle("new Title");
    }
}