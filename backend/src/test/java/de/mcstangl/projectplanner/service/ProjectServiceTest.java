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
import org.mockito.*;

import javax.persistence.EntityExistsException;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<ProjectEntity> projectEntityCaptor;

    private AutoCloseable closeable;

    @InjectMocks
    private ProjectService projectService;

    private ProjectEntity testProject;
    private UserEntity testUser1;
    private UserEntity testUser2;
    private UserEntity testUser3;

    @BeforeEach
    void setup() {

        closeable = MockitoAnnotations.openMocks(this);
        testUser1 = UserEntity.builder()
                .id(1L)
                .loginName("Test1")
                .role("ADMIN")
                .build();
        testUser2 = UserEntity.builder()
                .id(2L)
                .loginName("Test2")
                .role("ADMIN")
                .build();
        testUser3 = UserEntity.builder()
                .id(3L)
                .loginName("Test3")
                .role("ADMIN")
                .build();
        testProject = ProjectEntity.builder()
                .id(1L)
                .customer("Test")
                .title("Test")
                .dateOfReceipt(Date.valueOf("2021-09-12"))
                .owner(testUser1)
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("FindByName should return an optional project when the project is found")
    public void findByTitle() {
        // Given
        when(projectRepository.findByTitle(any())).thenReturn(
                Optional.of(testProject)
        );


        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get(), is(testProject));

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
                List.of(testProject)
        );

        // When
        List<ProjectEntity> actual = projectService.findAll();

        // Then
        assertThat(actual.size(), is(1));
        assertThat(actual, contains(testProject));
    }

    @Test
    @DisplayName("Creating a new project should return the newly created project")
    public void createNewProject() {
        // Given
        when(projectRepository.save(any())).thenReturn(
                testProject
        );

        // When
        ProjectEntity newProject = projectService.createNewProject(ProjectEntity.builder()
                .customer("Test")
                .title("Test")
                .dateOfReceipt(Date.valueOf("2021-09-12"))
                .build());

        // Then
        assertThat(newProject, is(testProject));

    }


    @Test
    @DisplayName("Creating a new project with a title that is already in DB should throw EntityExistsException")
    public void createNewProjectWithTitleThatAlreadyExists() {
        // Given
        when(projectRepository.save(any())).thenReturn(testProject);

        when(projectRepository.findByTitle(any())).thenReturn(
                Optional.of(testProject)
        );

        // When
        assertThrows(EntityExistsException.class, () -> projectService.createNewProject(ProjectEntity.builder()
                .customer("Test")
                .dateOfReceipt(Date.valueOf("2021-09-12"))
                .title("Test")
                .build()));
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForInvalidProjectTest")
    @DisplayName("Creating a new project with an invalid title or customer should throw IllegalArgumentException")
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
                        Optional.of(testProject));

        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(testUser2)
                .customer("New Customer")
                .dateOfReceipt(Date.valueOf("1999-01-01"))
                .title("Test")
                .build();
        // When
        projectService.update(projectEntity, newTitle);
        verify(projectRepository, times(1)).save(projectEntityCaptor.capture());
        ProjectEntity actual = projectEntityCaptor.getValue();

        // Then
        assertThat(actual.getTitle(), is("Test"));
        assertThat(actual.getCustomer(), is("New Customer"));
        assertNotNull(actual.getOwner());
        assertThat(actual.getOwner().getLoginName(), is("Test2"));
        assertThat(actual.getDateOfReceipt().toString(), is("1999-01-01"));
        assertNotNull(actual.getId());
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
                        Optional.of(testProject))
                .thenReturn(Optional.empty());

        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(testUser2)
                .customer("New Customer")
                .dateOfReceipt(Date.valueOf("1999-01-01"))
                .title("Test")
                .build();
        // When
        projectService.update(projectEntity, "new Title");

        verify(projectRepository, times(1)).save(projectEntityCaptor.capture());
        ProjectEntity actualSaved = projectEntityCaptor.getValue();

        verify(projectRepository, times(1)).delete(projectEntityCaptor.capture());
        String actualDeleted = projectEntityCaptor.getValue().getTitle();

        // Then
        assertThat(actualSaved.getTitle(), is("new Title"));
        assertThat(actualSaved.getCustomer(), is("New Customer"));
        assertNotNull(actualSaved.getOwner());
        assertThat(actualSaved.getOwner().getLoginName(), is("Test2"));
        assertThat(actualSaved.getDateOfReceipt().toString(), is("1999-01-01"));

        assertThat(actualDeleted, is("Test"));
    }

    @Test
    @DisplayName("Update should update writers")
    public void updateWriters() {
        // Given
        Set<UserEntity> writerSet = new HashSet<>();
        writerSet.add(testUser1);
        writerSet.add(testUser2);

        when(projectRepository.findByTitle("Test"))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(testUser3)
                                .customer("Test")
                                .writers(new HashSet<>())
                                .motionDesigners(new HashSet<>())
                                .title("Test")
                                .build()));


        when(userService.findByLoginName(any()))
                .thenReturn(
                        Optional.of(testUser1))
                .thenReturn(
                        Optional.of(testUser2));

        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(testUser3)
                .customer("Test")
                .writers(writerSet)
                .motionDesigners(new HashSet<>())
                .title("Test")
                .build();
        // When
        projectService.update(projectEntity, "Test");
        verify(projectRepository, times(1)).save(projectEntityCaptor.capture());
        Set<UserEntity> actual = projectEntityCaptor.getValue().getWriters();

        // Then
        assertThat(actual, containsInAnyOrder(testUser1, testUser2));
    }

    @Test
    @DisplayName("Update should update motion designers")
    public void updateMotionDesigners() {
        // Given
        Set<UserEntity> motionDesigners = new HashSet<>();
        motionDesigners.add(testUser1);
        motionDesigners.add(testUser2);

        when(projectRepository.findByTitle("Test"))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(testUser3)
                                .customer("Test")
                                .writers(new HashSet<>())
                                .motionDesigners(new HashSet<>())
                                .title("Test")
                                .build()));


        when(userService.findByLoginName(any()))
                .thenReturn(
                        Optional.of(testUser1))
                .thenReturn(
                        Optional.of(testUser2));


        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(testUser3)
                .customer("Test")
                .writers(new HashSet<>())
                .motionDesigners(motionDesigners)
                .title("Test")
                .build();

        // When
        projectService.update(projectEntity, "Test");

        // Then
        verify(projectRepository, times(1)).save(projectEntityCaptor.capture());
        Set<UserEntity> actual = projectEntityCaptor.getValue().getMotionDesigners();
        assertThat(actual, containsInAnyOrder(testUser1, testUser2));
    }
}