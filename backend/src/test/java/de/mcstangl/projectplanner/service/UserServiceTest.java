package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;

import javax.persistence.EntityExistsException;
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
        UserEntity adminUser = createAdminUser();
        adminUser.setId(null);
        adminUser.setPassword(null);

        when(userRepositoryMock.save(any())).thenReturn(adminUser);
        when(userRepositoryMock.findByLoginName(adminUser.getLoginName())).thenReturn(Optional.empty());

        // When
        UserEntity actual = userService.createNewUser(adminUser);

        verify(userRepositoryMock, times(1)).save(userEntityArgumentCaptor.capture());
        UserEntity argument = userEntityArgumentCaptor.getValue();

        // Then
        assertThat(argument, is(adminUser));
        assertNotNull(argument.getPassword());
        assertThat(actual, is(adminUser));
    }


    @ParameterizedTest
    @MethodSource("getArgumentsForCreateUserWithInvalidDataTest")
    @DisplayName("Create user with invalid data should throw an exception")
    public void createUserWithInvalidData(String loginName, UserRole userRole, Throwable throwable){
        // Given
        UserEntity userToSave = UserEntity.builder()
                .loginName(loginName)
                .role(userRole)
                .build();

        when(userRepositoryMock.findByLoginName("Test")).thenReturn(Optional.of(userToSave));

        // Then
        assertThrows(throwable.getClass(), ()-> userService.createNewUser(userToSave));
        verify(userRepositoryMock, times(0)).save(any());
    }

    private static Stream<Arguments> getArgumentsForCreateUserWithInvalidDataTest(){
        return Stream.of(
          Arguments.of(null, UserRole.ADMIN, new IllegalArgumentException()),
          Arguments.of("NewUser", null, new IllegalArgumentException()),
          Arguments.of("Test", UserRole.ADMIN, new EntityExistsException())
        );
    }


    @Test
    @DisplayName("Find all should return all user in DB")
    public void findAll(){
        // Given
        UserEntity adminUser = createAdminUser();
        UserEntity user = createUser();

        when(userRepositoryMock.findAll()).thenReturn(List.of(adminUser, user));

        // When
        List<UserEntity> actual = userService.findAll();

        // Then
        assertThat(actual.size(), is(2));
        assertThat(actual, containsInAnyOrder(adminUser, user));
    }

    @Test
    @DisplayName("Find user by login name should return the user found")
    public void findByLoginName(){
        // Given
        UserEntity adminUser = createAdminUser();

        when(userRepositoryMock.findByLoginName(adminUser.getLoginName())).thenReturn(Optional.of(adminUser));

        // When
        Optional<UserEntity> actual = userService.findByLoginName(adminUser.getLoginName());

        // Then
        assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find user by login should return an empty optional if the user is not in DB")
    public void findByUnknownLoginName(){
        // Given
        when(userRepositoryMock.findByLoginName("Unknown")).thenReturn(Optional.empty());

        // When
        Optional<UserEntity> actual = userService.findByLoginName("Unknown");

        // Then
        assertTrue(actual.isEmpty());
    }

    private UserEntity createAdminUser() {
        return UserEntity.builder()
                .id(1L)
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.ADMIN).build();
    }
    private UserEntity createUser(){
        return UserEntity.builder()
                .id(2L)
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role(UserRole.USER).build();
    }
}