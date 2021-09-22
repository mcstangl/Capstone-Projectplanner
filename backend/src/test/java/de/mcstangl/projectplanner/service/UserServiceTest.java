package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserService userService;


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
    @DisplayName("Find all shoul return all user in DB")
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