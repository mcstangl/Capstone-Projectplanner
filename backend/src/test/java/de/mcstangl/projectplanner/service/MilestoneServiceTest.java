package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.persistence.EntityExistsException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MilestoneServiceTest {

    @Mock
    private MilestoneRepository milestoneRepositoryMock;

    @Mock
    private ProjectService projectServiceMock;

    @InjectMocks
    private MilestoneService mileStoneService;


    private AutoCloseable closeable;

    @Captor
    private ArgumentCaptor<ProjectEntity> projectEntityArgumentCaptor;

    @Captor
    private ArgumentCaptor<MilestoneEntity> milestoneEntityArgumentCaptor;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Find by project title should return all milestones found")
    public void findAllByProjectTitle() {
        // Given
        when(projectServiceMock.findByTitle("Test")).thenReturn(
                Optional.of(ProjectEntity.builder().title("Test").id(1L).build())
        );

        // When
        mileStoneService.findAllByProjectTitle("Test");

        // Then
        verify(milestoneRepositoryMock, times(1))
                .findAllByProjectEntity(projectEntityArgumentCaptor.capture());

        ProjectEntity capturedArgument = projectEntityArgumentCaptor.getValue();

        assertThat(capturedArgument.getTitle(), is("Test"));
    }


    @Test
    @DisplayName("Create new milestone should persist the milestone")
    public void createNewMilestone() {
        // Given
        when(projectServiceMock.findByTitle("Test")).thenReturn(
                Optional.of(ProjectEntity.builder().title("Test").id(1L).build())
        );
        MilestoneEntity testMilestone1 = getTestMilestone();

        // When
        mileStoneService.createNewMilestone(testMilestone1);

        // Then
        verify(milestoneRepositoryMock, times(1)).save(testMilestone1);
    }


    @Test
    @DisplayName("Create new milestone with a milestone that has an id should fail")
    public void createNewMilestoneWithId() {
        //Given
        MilestoneEntity testMilestone = MilestoneEntity.builder()
                .id(1L)
                .projectEntity(ProjectEntity.builder().title("Test").build())
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test1")
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> mileStoneService.createNewMilestone(testMilestone));
        verifyNoInteractions(milestoneRepositoryMock);
    }

    @Test
    @DisplayName("Create new milestone with a milestone title that already exits for the project should fail")
    public void createNewMilestoneWithAnExistingTitle() {
        // Given
        when(milestoneRepositoryMock.findAllByProjectEntity(any())).thenReturn(
                List.of(getTestMilestone())
        );
        when(projectServiceMock.findByTitle("Test")).thenReturn(
                Optional.of(ProjectEntity.builder().title("Test").id(1L).build())
        );

        // When
        assertThrows(EntityExistsException.class, () -> mileStoneService.createNewMilestone(MilestoneEntity.builder()
                .projectEntity(ProjectEntity.builder().title("Test").build())
                .title("Test1")
                .build()));

    }


    @Test
    @DisplayName("Update a milestone should change all fields")
    public void update() {
        // When
        mileStoneService.updateMilestone(MilestoneEntity.builder()
                .id(1L)
                .projectEntity(ProjectEntity.builder().title("Test").build())
                .dateFinished(Date.valueOf("2021-01-01"))
                .dueDate(Date.valueOf("2021-01-01"))
                .title("New Title")
                .build());
        verify(milestoneRepositoryMock, times(1)).save(milestoneEntityArgumentCaptor.capture());
        MilestoneEntity actual = milestoneEntityArgumentCaptor.getValue();

        // Then
        assertThat(actual.getId(), is(1L));
        assertThat(actual.getTitle(), is("New Title"));
        assertThat(actual.getDateFinished().toString(), is("2021-01-01"));
        assertThat(actual.getDueDate().toString(), is("2021-01-01"));
        assertThat(actual.getProjectEntity(), is(ProjectEntity.builder().title("Test").build()));
    }

    private MilestoneEntity getTestMilestone() {
        return MilestoneEntity.builder()
                .projectEntity(ProjectEntity.builder().title("Test").build())
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test1")
                .build();
    }
}