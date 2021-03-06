package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityArgumentCaptor;

    private AutoCloseable closeable;


    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("Create user should return the created user")
    public void createNewUser() {
        // Given
        UserEntity adminUser = createTestAdminUser();
        adminUser.setId(null);
        adminUser.setPassword(null);

        when(passwordService.getRandomPassword()).thenReturn("RandomPassword");
        when(passwordService.getHashedPassword("RandomPassword")).thenReturn("HashedPassword");
        when(userRepositoryMock.save(any())).thenReturn(adminUser);
        when(userRepositoryMock.findByLoginName(adminUser.getLoginName())).thenReturn(Optional.empty());

        // When
        UserEntity actual = userService.createNewUser(adminUser);

        verify(userRepositoryMock, times(1)).save(userEntityArgumentCaptor.capture());
        UserEntity argument = userEntityArgumentCaptor.getValue();

        // Then
        assertThat(argument, is(adminUser));
        assertThat(argument.getPassword(), is("HashedPassword"));

        assertThat(actual.getPassword(), is("RandomPassword"));
        assertThat(actual, is(adminUser));
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForCreateUserWithInvalidDataTest")
    @DisplayName("Create user with invalid data should throw an exception")
    public void createUserWithInvalidData(String loginName, UserRole userRole, Throwable throwable) {
        // Given
        UserEntity userToSave = UserEntity.builder()
                .loginName(loginName)
                .role(userRole)
                .build();

        when(userRepositoryMock.findByLoginName("Test")).thenReturn(Optional.of(userToSave));

        // Then
        assertThrows(throwable.getClass(), () -> userService.createNewUser(userToSave));
        verify(userRepositoryMock, times(0)).save(any());
    }

    private static Stream<Arguments> getArgumentsForCreateUserWithInvalidDataTest() {
        return Stream.of(
                Arguments.of(null, UserRole.ADMIN, new IllegalArgumentException()),
                Arguments.of("NewUser", null, new IllegalArgumentException()),
                Arguments.of("Test", UserRole.ADMIN, new EntityExistsException())
        );
    }


    @Test
    @DisplayName("Find all should return all user in DB")
    public void findAll() {
        // Given
        UserEntity adminUser = createTestAdminUser();
        UserEntity user = createTestUser();

        when(userRepositoryMock.findAll()).thenReturn(List.of(adminUser, user));

        // When
        List<UserEntity> actual = userService.findAll();

        // Then
        assertThat(actual.size(), is(2));
        assertThat(actual, containsInAnyOrder(adminUser, user));
    }

    @Test
    @DisplayName("Find user by login name should return the user found")
    public void findByLoginName() {
        // Given
        UserEntity adminUser = createTestAdminUser();

        when(userRepositoryMock.findByLoginName(adminUser.getLoginName())).thenReturn(Optional.of(adminUser));

        // When
        Optional<UserEntity> actual = userService.findByLoginName(adminUser.getLoginName());

        // Then
        assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find user by login should return an empty optional if the user is not in DB")
    public void findByUnknownLoginName() {
        // Given
        when(userRepositoryMock.findByLoginName("Unknown")).thenReturn(Optional.empty());

        // When
        Optional<UserEntity> actual = userService.findByLoginName("Unknown");

        // Then
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Update user should update all fields and return the updated user")
    public void updateUser() {
        // Given
        UserEntity user = createTestUser();

        UserEntity userUpdateData = UserEntity.builder()
                .loginName("New Name")
                .role(UserRole.ADMIN)
                .build();
        when(userRepositoryMock.findByLoginName(user.getLoginName())).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(any())).thenReturn(
                UserEntity.builder()
                        .id(user.getId())
                        .loginName("New Name")
                        .role(UserRole.ADMIN)
                        .build());

        // When
        UserEntity actual = userService.updateUser(user.getLoginName(), userUpdateData);

        verify(userRepositoryMock, times(1)).save(userEntityArgumentCaptor.capture());
        UserEntity argument = userEntityArgumentCaptor.getValue();

        // Then
        assertThat(actual, is(UserEntity.builder()
                .id(user.getId())
                .loginName("New Name")
                .role(UserRole.ADMIN)
                .build()));

        assertThat(argument.getLoginName(), is(userUpdateData.getLoginName()));
        assertThat(argument.getRole(), is(userUpdateData.getRole()));
        assertThat(argument.getId(), is(user.getId()));
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForUpdateUserWithInvalidDataTest")
    @DisplayName("Create user with invalid data should throw an exception")
    public void updateUserWithInvalidData(String loginName, String newName, Throwable throwable) {
        // Given
        UserEntity testUser = createTestUser();
        UserEntity testAdminUser = createTestAdminUser();


        UserEntity userUpdateData = UserEntity.builder()
                .loginName(newName)
                .role(UserRole.ADMIN)
                .build();

        when(userRepositoryMock.findByLoginName("Unknown")).thenReturn(Optional.empty());
        when(userRepositoryMock.findByLoginName("Dave")).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.findByLoginName("Hans")).thenReturn(Optional.of(testAdminUser));

        // Then
        assertThrows(throwable.getClass(), () -> userService.updateUser(loginName, userUpdateData));
        verify(userRepositoryMock, times(0)).save(any());
    }

    private static Stream<Arguments> getArgumentsForUpdateUserWithInvalidDataTest() {
        return Stream.of(
                Arguments.of("Hans", null, new IllegalArgumentException()),
                Arguments.of("Hans", "Dave", new EntityExistsException()),
                Arguments.of("Unknown", "New Name", new EntityNotFoundException())


        );
    }

    @Test
    @DisplayName("Reset password should return a user with random password")
    public void resetPassword() {
        // Given
        UserEntity testUser = createTestUser();

        when(userRepositoryMock.findByLoginName(testUser.getLoginName())).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.save(any())).thenReturn(testUser);
        when(passwordService.getRandomPassword()).thenReturn("RandomPassword");
        when(passwordService.getHashedPassword("RandomPassword")).thenReturn("HashedPassword");

        // When
        UserEntity actual = userService.resetPassword(testUser.getLoginName());

        verify(userRepositoryMock, times(1)).save(userEntityArgumentCaptor.capture());
        UserEntity argument = userEntityArgumentCaptor.getValue();

        // Then
        assertThat(actual.getPassword(), is("RandomPassword"));
        assertThat(argument.getPassword(), is("HashedPassword"));
    }

    @Test
    @DisplayName("Delete user should delete return deletes user")
    public void delete() {
        // Given
        UserEntity testUser = createTestUser();

        when(userRepositoryMock.findByLoginName(testUser.getLoginName())).thenReturn(Optional.of(testUser));

        // When
        UserEntity actual = userService.deleteUserByLoginName(testUser.getLoginName());

        verify(userRepositoryMock, times(1)).delete(userEntityArgumentCaptor.capture());
        UserEntity argument = userEntityArgumentCaptor.getValue();

        // Then
        assertThat(actual, is(testUser));
        assertThat(argument, is(testUser));
    }

    @Test
    @DisplayName("Delete an unknown user should throw EntityNotFoundException")
    public void deleteUnknownUser() {
        // Given
        when(userRepositoryMock.findByLoginName("Unknown")).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserByLoginName("Unknown"));
    }

    @ParameterizedTest
    @MethodSource("getArgumentsForDeleteUserWithProjectsTest")
    @DisplayName("Delete user should throw an IllegalArgumentException if the user has a relation to any project")
    public void deleteUserWithProjects(List<ProjectEntity> projectList1,  List<ProjectEntity> projectList2, List<ProjectEntity> projectList3) {
        // Given
        UserEntity testUser = createTestUser();
        testUser.setOwnedProjects(projectList1);
        testUser.setWriterInProjects(projectList2);
        testUser.setMotionDesignerInProjects(projectList3);

        when(userRepositoryMock.findByLoginName(testUser.getLoginName())).thenReturn(Optional.of(testUser));

        // Then
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserByLoginName(testUser.getLoginName()));
    }


    public static Stream<Arguments> getArgumentsForDeleteUserWithProjectsTest(){
        List<ProjectEntity> testProjectList = List.of(ProjectEntity.builder()
                .title("Test")
                .id(1L)
                .build());
        return Stream.of(
                Arguments.of(null, null,testProjectList),
                Arguments.of(null, testProjectList,null),
                Arguments.of(testProjectList, null, null)
        );
    }

    private UserEntity createTestAdminUser() {
        return UserEntity.builder()
                .id(1L)
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.ADMIN).build();
    }

    private UserEntity createTestUser() {
        return UserEntity.builder()
                .id(2L)
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.USER).build();
    }
}