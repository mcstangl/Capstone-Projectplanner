package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.ProjectStatus;
import de.mcstangl.projectplanner.model.MilestoneEntity;
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
import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private MilestoneService milestoneServiceMock;

    @Captor
    private ArgumentCaptor<ProjectEntity> projectEntityCaptor;

    private AutoCloseable closeable;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("FindByName should return an optional project when the project is found")
    public void findByTitle() {
        // Given
        ProjectEntity testProject = createTestProject();
        when(projectRepositoryMock.findByTitle(any())).thenReturn(
                Optional.of(testProject)
        );


        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get(), is(testProject));

    }

    @Test
    @DisplayName("FindByName should return an optional project with the milesStones sorted by dueDate")
    public void findByTitleWithSortedMilestones() {
        // Given
        ProjectEntity testProject = createTestProject();


        MilestoneEntity firstMilestone = MilestoneEntity.builder()
                .id(1L)
                .dueDate(Date.valueOf("2021-01-01"))
                .title("Test1")
                .build();
        MilestoneEntity secondMilestone = MilestoneEntity.builder()
                .id(2L)
                .dueDate(Date.valueOf("2021-02-02"))
                .title("Test2")
                .build();
        MilestoneEntity thirdMilestone = MilestoneEntity.builder()
                .id(3L)
                .dueDate(Date.valueOf("2021-02-03"))
                .title("Test2")
                .build();

        List<MilestoneEntity> milestoneEntitySet = new ArrayList<>();
        milestoneEntitySet.add(thirdMilestone);
        milestoneEntitySet.add(firstMilestone);
        milestoneEntitySet.add(secondMilestone);
        testProject.setMilestones(milestoneEntitySet);

        when(projectRepositoryMock.findByTitle(any())).thenReturn(
                Optional.of(testProject)
        );

        // When
        Optional<ProjectEntity> actualOptional = projectService.findByTitle("Test");

        // Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get().getMilestones(), contains(firstMilestone,secondMilestone,thirdMilestone));

    }

    @Test
    @DisplayName("FindByName should return an optional empty when the project is not found")
    public void findByTitleUnknown() {
        // Given
        when(projectRepositoryMock.findByTitle(any())).thenReturn(
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
        ProjectEntity testProject = createTestProject();
        when(projectRepositoryMock.findAll()).thenReturn(
                List.of(testProject)
        );

        when(milestoneServiceMock.getAllSortedByDueDate()).thenReturn(List.of());

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
        ProjectEntity testProject = createTestProject();

        // When
        ProjectEntity newProject = projectService.createNewProject(testProject);

        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        ProjectEntity actual = projectEntityCaptor.getValue();
        // Then
        assertThat(actual, is(testProject));
        assertThat(actual.getStatus(), is(ProjectStatus.OPEN));
    }


    @Test
    @DisplayName("Creating a new project with a title that is already in DB should throw EntityExistsException")
    public void createNewProjectWithTitleThatAlreadyExists() {
        // Given
        ProjectEntity testProject = createTestProject();
        when(projectRepositoryMock.save(any())).thenReturn(testProject);

        when(projectRepositoryMock.findByTitle(any())).thenReturn(
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
        ProjectEntity testProject = createTestProject();
        UserEntity testUser2 = createTestUser2();
        when(projectRepositoryMock.findByTitle(any()))
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
        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        ProjectEntity actual = projectEntityCaptor.getValue();

        // Then
        assertThat(actual.getTitle(), is("Test"));
        assertThat(actual.getCustomer(), is("New Customer"));
        assertNotNull(actual.getOwner());
        assertThat(actual.getOwner().getLoginName(), is("Test2"));
        assertThat(actual.getStatus(), is(ProjectStatus.OPEN));
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
        ProjectEntity testProject = createTestProject();
        UserEntity testUser2 = createTestUser2();

        when(projectRepositoryMock.findByTitle("Test"))
                .thenReturn(
                        Optional.of(testProject));


        ProjectEntity projectEntity = ProjectEntity.builder()
                .owner(testUser2)
                .customer("New Customer")
                .dateOfReceipt(Date.valueOf("1999-01-01"))
                .title("Test")
                .build();
        // When
        projectService.update(projectEntity, "new Title");

        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        ProjectEntity actualSaved = projectEntityCaptor.getValue();

        // Then
        assertThat(actualSaved.getTitle(), is("new Title"));
        assertThat(actualSaved.getCustomer(), is("New Customer"));
        assertNotNull(actualSaved.getOwner());
        assertThat(actualSaved.getStatus(), is(ProjectStatus.OPEN));
        assertThat(actualSaved.getOwner().getLoginName(), is("Test2"));
        assertThat(actualSaved.getDateOfReceipt().toString(), is("1999-01-01"));
    }

    @Test
    @DisplayName("Update should update writers")
    public void updateWriters() {
        // Given
        UserEntity testUser1 = createTestUser1();
        UserEntity testUser2 = createTestUser2();
        UserEntity testUser3 = createTestUser3();
        Set<UserEntity> writerSet = new HashSet<>();
        writerSet.add(testUser1);
        writerSet.add(testUser2);

        when(projectRepositoryMock.findByTitle("Test"))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(testUser3)
                                .customer("Test")
                                .writers(new HashSet<>())
                                .motionDesigners(new HashSet<>())
                                .title("Test")
                                .build()));


        when(userServiceMock.findByLoginName(any()))
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
        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        Set<UserEntity> actual = projectEntityCaptor.getValue().getWriters();

        // Then
        assertThat(actual, containsInAnyOrder(testUser1, testUser2));
    }

    @Test
    @DisplayName("Update should update motion designers")
    public void updateMotionDesigners() {
        // Given
        UserEntity testUser1 = createTestUser1();
        UserEntity testUser2 = createTestUser2();
        UserEntity testUser3 = createTestUser3();
        Set<UserEntity> motionDesigners = new HashSet<>();
        motionDesigners.add(testUser1);
        motionDesigners.add(testUser2);

        when(projectRepositoryMock.findByTitle("Test"))
                .thenReturn(
                        Optional.of(ProjectEntity.builder()
                                .id(1L)
                                .owner(testUser3)
                                .customer("Test")
                                .writers(new HashSet<>())
                                .motionDesigners(new HashSet<>())
                                .title("Test")
                                .build()));


        when(userServiceMock.findByLoginName(any()))
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
        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        Set<UserEntity> actual = projectEntityCaptor.getValue().getMotionDesigners();
        assertThat(actual, containsInAnyOrder(testUser1, testUser2));
    }

    @Test
    @DisplayName("Move to archive should set project status to ARCHIVE")
    public void moveToArchive(){
        // Given
        ProjectEntity testProject = createTestProject();

        when(projectRepositoryMock.findByTitle(any())).thenReturn(Optional.of(testProject));

        // When
        projectService.moveToArchive("Test");

        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        ProjectStatus actual = projectEntityCaptor.getValue().getStatus();

        // Then
        assertThat(actual, is(ProjectStatus.ARCHIVE));
    }

    @Test
    @DisplayName("Move to archive should throw an EntityNotFoundException when project is not in DB")
    public void moveToArchiveWithUnknownProjectTitle(){
        // Given
        when(projectRepositoryMock.findByTitle(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class,() -> projectService.moveToArchive("Unknown"));

    }

    @Test
    @DisplayName("Restore project should set project status to OPEN")
    public void restoreProject(){
        // Given
        ProjectEntity testProject = createTestProject();

        when(projectRepositoryMock.findByTitle(any())).thenReturn(Optional.of(testProject));

        // When
        projectService.restoreFromArchive("Test");

        verify(projectRepositoryMock, times(1)).save(projectEntityCaptor.capture());
        ProjectStatus actual = projectEntityCaptor.getValue().getStatus();

        // Then
        assertThat(actual, is(ProjectStatus.OPEN));
    }

    @Test
    @DisplayName("Move to archive should throw an EntityNotFoundException when project is not in DB")
    public void restoreProjectWithUnknownProjectTitle(){
        // Given
        when(projectRepositoryMock.findByTitle(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class,() -> projectService.moveToArchive("Unknown"));

    }

    private UserEntity createTestUser1() {
        return UserEntity.builder()
                .id(1L)
                .loginName("Test1")
                .role("ADMIN")
                .build();
    }

    private UserEntity createTestUser2() {
        return UserEntity.builder()
                .id(2L)
                .loginName("Test2")
                .role("ADMIN")
                .build();
    }

    private UserEntity createTestUser3() {
        return UserEntity.builder()
                .id(3L)
                .loginName("Test3")
                .role("ADMIN")
                .build();
    }

    private ProjectEntity createTestProject() {
        return ProjectEntity.builder()
                .id(1L)
                .customer("Test")
                .title("Test")
                .dateOfReceipt(Date.valueOf("2021-09-12"))
                .owner(createTestUser1())
                .build();

    }
}