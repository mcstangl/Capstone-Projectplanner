package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MilestoneServiceTest {

    @Mock
    private MilestoneRepository milestoneRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

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
        when(projectRepositoryMock.findByTitle("Test")).thenReturn(
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
        when(projectRepositoryMock.findByTitle("Test")).thenReturn(
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
        when(projectRepositoryMock.findByTitle("Test")).thenReturn(
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

    @Test
    @DisplayName("Delete milestone should delete milestone from DB")
    public void deleteMilestoneById() {
        // Given
        MilestoneEntity testMilestone = getTestMilestone();
        Long idToDelete = testMilestone.getId();

        when(milestoneRepositoryMock.findById(idToDelete))
                .thenReturn(Optional.of(testMilestone));

        when(projectRepositoryMock.findByTitle(any()))
                .thenReturn(Optional.of(ProjectEntity.builder().title("Test").id(1L).milestones(new ArrayList<>(List.of(testMilestone))).build()));

        // When
        MilestoneEntity actual = mileStoneService.deleteById(idToDelete);

        // Then
        assertThat(actual, is(testMilestone));
        verify(projectRepositoryMock, times(1)).save(projectEntityArgumentCaptor.capture());
        List<MilestoneEntity> actualMilestoneList = projectEntityArgumentCaptor.getValue().getMilestones();
        assertTrue(actualMilestoneList.isEmpty());

    }

    @Test
    @DisplayName("Delete by id should throw and EntityNotFoundException when milestone is not in DB")
    public void deleteMilestoneWithNonExistingId(){
        // Given
        MilestoneEntity testMilestone = getTestMilestone();
        Long idToDelete = testMilestone.getId();

        when(milestoneRepositoryMock.findById(idToDelete))
                .thenReturn(Optional.empty());

        when(projectRepositoryMock.findByTitle(any()))
                .thenReturn(Optional.of(ProjectEntity.builder().title("Test").id(1L).milestones(new ArrayList<>(List.of(testMilestone))).build()));

        // Then
        assertThrows(EntityNotFoundException.class, ()-> mileStoneService.deleteById(1L));

    }

    @Test
    @DisplayName("Delete by id should throw and EntityNotFoundException when the project is not in DB")
    public void deleteMilestoneWithNonExistingProject(){
        MilestoneEntity testMilestone = getTestMilestone();
        Long idToDelete = testMilestone.getId();

        when(milestoneRepositoryMock.findById(idToDelete))
                .thenReturn(Optional.of(testMilestone));

        when(projectRepositoryMock.findByTitle(any()))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, ()-> mileStoneService.deleteById(1L));

    }


    @Test
    @DisplayName("Sort milestone by dueDate should return a sorted list")
    public void sortMilestonesByDueDate(){
        // Given
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
                .title("Test3")
                .build();

        // When
        List<MilestoneEntity> actual = mileStoneService.sortMilestonesByDueDate(List.of(thirdMilestone, secondMilestone, firstMilestone));

        // Then
        assertThat(actual, contains(firstMilestone, secondMilestone, thirdMilestone));

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