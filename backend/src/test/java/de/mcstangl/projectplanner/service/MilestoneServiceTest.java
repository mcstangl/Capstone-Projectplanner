package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    private MilestoneEntity testMilestone1;

    @BeforeEach
    public void setup(){
        closeable = MockitoAnnotations.openMocks(this);

        when(projectServiceMock.findByTitle("Test")).thenReturn(
                Optional.of(ProjectEntity.builder().title("Test").id(1L).build())
        );

        testMilestone1 = MilestoneEntity.builder()
                .projectEntity(ProjectEntity.builder().title("Test").build())
                .dateFinished(Date.valueOf("2021-12-12"))
                .dueDate(Date.valueOf("2021-03-13"))
                .title("Test1")
                .build();
    }

    @AfterEach
    public void tearDown() throws Exception{
        closeable.close();
    }

    @Test
    @DisplayName("Find by project title should return all milestones found")
    public void findAllByProjectTitle(){
        // When
        mileStoneService.findAllByProjectTitle("Test");

        verify(milestoneRepositoryMock,times(1)).findAllByProjectEntity(projectEntityArgumentCaptor.capture());
        ProjectEntity capturedArgument = projectEntityArgumentCaptor.getValue();

        // Then
        assertThat(capturedArgument.getTitle(), is("Test"));
    }


    @Test
    @DisplayName("Create new milestone should persist the milestone")
    public void createNewMilestone(){
        // When
        mileStoneService.createNewMileStone(testMilestone1);

        // Then
        verify(milestoneRepositoryMock, times(1)).save(testMilestone1);
    }
}