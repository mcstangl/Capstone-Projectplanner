package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.enums.ProjectStatus;
import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import de.mcstangl.projectplanner.repository.UserRepository;
import de.mcstangl.projectplanner.util.TestUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTest extends SpringBootTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtil testUtil;


    @AfterEach
    public void clear() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Creating a new project should persist and return the newly created project")
    public void createNewProject() {
        // Given
        createTestUser1();

        ProjectDto projectDto = ProjectDto.builder()
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .title("Test Title")
                .customer("Test Customer")
                .dateOfReceipt("2021-09-13")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is("Test Title"));
        assertThat(response.getBody().getCustomer(), is("Test Customer"));
        assertThat(response.getBody().getStatus(), is("OPEN"));
        assertThat(response.getBody().getDateOfReceipt(), is("2021-09-13"));
    }

    @Test
    @DisplayName("Creating a new project as USER should return HttpStatus.UNAUTHORIZED")
    public void createNewProjectAsUserShouldFail() {
        // Given
        ProjectDto projectDto = ProjectDto.builder()
                .title("Test Title")
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .customer("Test Customer")
                .dateOfReceipt("2120-12-12")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, testUtil.getAuthHeader("USER")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Creating a new project with a title that is already in DB should return HttpStatus.CONFLICT")
    public void createProjectWithTitleThatAlreadyExists() {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        ProjectDto projectDto = ProjectDto.builder()
                .title("Test")
                .owner(UserDto.builder()
                        .loginName("Test")
                        .role("ADMIN").build())
                .customer("Test")
                .dateOfReceipt("2021-09-12")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForBadRequestTest")
    @DisplayName("Creating a new project with a invalid parameters should return HttpStatus.BAD_REQUEST or NOT_FOUND")
    public void createProjectWithBadRequest(String title, String customer, UserDto user, String date, HttpStatus expected) {
        // Given
        createTestUser1();
        ProjectDto projectDto = ProjectDto.builder()
                .title(title)
                .owner(user)
                .dateOfReceipt(date)
                .customer(customer)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(projectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(expected));
    }

    private static Stream<Arguments> getArgumentsForBadRequestTest() {
        UserDto userDto = UserDto.builder()
                .loginName("Test")
                .role("ADMIN").build();
        UserDto unknownUser = UserDto.builder()
                .loginName("Unknown")
                .role("ADMIN").build();
        return Stream.of(
                Arguments.of("", "Test", userDto, "1999-01-01", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "", userDto, "1999-01-01", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", null, userDto, "1999-01-01", HttpStatus.BAD_REQUEST),
                Arguments.of(null, "Test", userDto, "1999-01-01", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", null, "1999-01-01", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", userDto, "1999-1", HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", userDto, null, HttpStatus.BAD_REQUEST),
                Arguments.of("Test", "Test", unknownUser, "1999-01-01", HttpStatus.NOT_FOUND)
        );
    }

    @Test
    @DisplayName("Find all should return a list of all projects in DB")
    public void findAll() {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto[]> response = testRestTemplate.exchange(
                getUrl(),
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto[].class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().length, is(1));
        assertThat(Arrays.stream(response.getBody()).toList(), contains(ProjectDto.builder()
                .title("Test")
                .build()));
    }

    @Test
    @DisplayName("Find by title should return project found")
    public void findByTitle() {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus(), is("OPEN"));
        assertThat(response.getBody().getTitle(), is("Test"));
    }

    @Test
    @DisplayName("Find by title should return HttpStatus.NOT_FOUND if project is not in DB")
    public void findByUnknownTitle() {
        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown",
                HttpMethod.GET,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class
        );

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectTest")
    @DisplayName("Update Project should update all fields except title when there is no new title")
    public void updateProject(String newTitle, String expectedTitle) {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestUser2();
        createTestProject(testUser1);

        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(UserDto.builder().loginName("Other User").role("ADMIN").build())
                .customer("New Customer")
                .title("Test")
                .dateOfReceipt("2021-09-13")
                .newTitle(newTitle)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getTitle(), is(expectedTitle));
        assertThat(response.getBody().getOwner().getLoginName(), is("Other User"));
        assertThat(response.getBody().getCustomer(), is("New Customer"));
        assertThat(response.getBody().getStatus(), is("OPEN"));
        assertThat(response.getBody().getDateOfReceipt(), is("2021-09-13"));
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectTest() {
        return Stream.of(
                Arguments.of("Test", "Test"),
                Arguments.of("New Title", "New Title")
        );
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForWritersOfProjectTest")
    @DisplayName("Update Project should update the list of writers")
    public void updateWritersOfProject(List<UserDto> writers, int expectedLength) {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestUser2();
        createTestProject(testUser1);

        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(UserDto.builder().loginName("Other User").role("ADMIN").build())
                .customer("New Customer")
                .title("Test")
                .newTitle("Test")
                .dateOfReceipt("2021-09-13")
                .writer(writers)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getWriter());
        assertThat(response.getBody().getWriter().size(), is(expectedLength));
        assertThat(response.getBody().getWriter(), containsInRelativeOrder(writers.get(0)));
    }

    private static Stream<Arguments> getArgumentsForWritersOfProjectTest() {
        UserDto firstWriter = UserDto.builder()
                .loginName("Test")
                .role("ADMIN").build();
        UserDto otherWriter = UserDto.builder()
                .loginName("Other User")
                .role("ADMIN").build();
        List<UserDto> writersToAdd = List.of(firstWriter, otherWriter);
        List<UserDto> writersToAddWithDouble = List.of(firstWriter, firstWriter);

        return Stream.of(
                Arguments.of(writersToAdd, 2),
                Arguments.of(writersToAddWithDouble, 1)
        );
    }



    @ParameterizedTest
    @MethodSource("getArgumentsForMotionDesignerOfProjectTest")
    @DisplayName("Update Project should update the list of writers")
    public void updateMotionDesignersOfProject(List<UserDto> motionDesigners, int expectedLength) {
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestUser2();
        createTestProject(testUser1);

        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(UserDto.builder().loginName("Other User").role("ADMIN").build())
                .customer("New Customer")
                .title("Test")
                .newTitle("Test")
                .dateOfReceipt("2021-09-13")
                .motionDesign(motionDesigners)
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMotionDesign());
        assertThat(response.getBody().getMotionDesign().size(), is(expectedLength));
        assertThat(response.getBody().getMotionDesign(), containsInRelativeOrder(motionDesigners.get(0)));
    }

    private static Stream<Arguments> getArgumentsForMotionDesignerOfProjectTest() {
        UserDto firstMotionDesigner = UserDto.builder()
                .loginName("Test")
                .role("ADMIN").build();
        UserDto secondMotionDesigner = UserDto.builder()
                .loginName("Other User")
                .role("ADMIN").build();
        List<UserDto> motionDesignerToAdd = List.of(firstMotionDesigner, secondMotionDesigner);
        List<UserDto> motionDesignerToAddWithDouble = List.of(firstMotionDesigner, firstMotionDesigner);

        return Stream.of(
                Arguments.of(motionDesignerToAdd, 2),
                Arguments.of(motionDesignerToAddWithDouble, 1)
        );
    }

    @Test
    @DisplayName("Update Project should return HttpStatus.BAD_REQUEST if path variable and project title don't match")
    public void updateProjectWithNonMatchingPathVariable() {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .customer("New Customer")
                .title("Test")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/DoesNotMatchTitle",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateProjectWithoutOwnerTest")
    @DisplayName("Update Project without an owner should return HttpStatus.BAD_REQUEST")
    public void updateProjectWithoutOwner(UserDto owner) {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .owner(owner)
                .title("Test")
                .dateOfReceipt("1999-01-01")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/DoesNotMatchTitle",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private static Stream<Arguments> getArgumentsForUpdateProjectWithoutOwnerTest() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(UserDto.builder()
                        .loginName("Unknown")
                        .role("ADMIN")
                        .build())
        );
    }

    @Test
    @DisplayName("Update Project with a bad date or receipt should return HttpStatus.BAD_REQUEST")
    public void updateProjectWithBadDateOfReceipt() {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .customer("New Customer")
                .title("Test")
                .dateOfReceipt("not-a-date")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    @Test
    @DisplayName("Update Project should return HttpStatus.UNAUTHORIZED if user is not an admin")
    public void updateProjectAsUserShouldFail() {
        // Given
        UpdateProjectDto updateProjectDto = UpdateProjectDto.builder()
                .customer("New Customer")
                .title("Test")
                .dateOfReceipt("1999-01-01")
                .newTitle("newTitle")
                .build();

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test",
                HttpMethod.PUT,
                new HttpEntity<>(updateProjectDto, testUtil.getAuthHeader("USER")),
                ProjectDto.class);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    @DisplayName("Move to archive should set the project status to archive")
    public void moveToArchive(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test/archive",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus(), is("ARCHIVE"));
    }

    @Test
    @DisplayName("Move to archive should return HttpStatus.NOT_FOUND when the project is not in DB")
    public void moveToArchiveWithUnknownProjectTitle(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown/archive",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Move to archive as non admin user should return HttpStatus.UNAUTHORIZED")
    public void moveToArchiveAsUser(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown/archive",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }


    @Test
    @DisplayName("Restore Project should set the project status to open")
    public void restoreProject(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Test/restore",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(response.getBody());
        assertThat(response.getBody().getStatus(), is("OPEN"));
    }

    @Test
    @DisplayName("Restore project should return HttpStatus.NOT_FOUND when the project is not in DB")
    public void restoreProjectWithUnknownProjectTitle(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown/archive",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("ADMIN")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("Restore project as non admin user should return HttpStatus.UNAUTHORIZED")
    public void restoreProjectAsUser(){
        // Given
        UserEntity testUser1 = createTestUser1();
        createTestProject(testUser1);

        // When
        ResponseEntity<ProjectDto> response = testRestTemplate.exchange(
                getUrl() + "/Unknown/archive",
                HttpMethod.PUT,
                new HttpEntity<>(null, testUtil.getAuthHeader("USER")),
                ProjectDto.class);
        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }


    private String getUrl() {
        return String.format("http://localhost:%s/api/project-planner/project", port);
    }


    public UserEntity createTestUser1() {
        return userRepository.save(UserEntity.builder()
                .id(1L)
                .loginName("Test")
                .password("Test")
                .role(UserRole.ADMIN).build());
    }

    public void createTestUser2() {
        userRepository.save(UserEntity.builder()
                .id(2L)
                .loginName("Other User")
                .password("Test")
                .role(UserRole.ADMIN).build());
    }

    public void createTestProject(UserEntity testUser){

        projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .id(1L)
                        .title("Test")
                        .dateOfReceipt(java.sql.Date.valueOf("2012-03-21"))
                        .owner(testUser)
                        .status(ProjectStatus.OPEN)
                        .customer("Test").build()

        );
    }

}