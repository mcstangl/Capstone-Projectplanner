package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest extends SpringBootTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName("Save should persist user in DB")
    public void save(){
        // Given
        UserEntity testUser = createUser();

        //When
       UserEntity actual = userRepository.save(testUser);

        //Then
        assertNotNull(actual.getId());
        assertThat(actual.getRole(), is("USER"));
        assertThat(actual.getLoginName(), is("Dave"));
    }


    @Test
    @Transactional
    @DisplayName("Find user by loginName should return the user found")
    public void findUserByLoginName(){
        // Given
        createAdminUser();

        //When
        Optional<UserEntity> actualOptional = userRepository.findByLoginName("Hans");

        //Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get().getRole(), is("ADMIN"));
        assertThat(actualOptional.get().getLoginName(), is("Hans"));
    }

    @Test
    @Transactional
    @DisplayName("Find user by unknown loginName should return an empty optional")
    public void findUserByUnknownName(){
        //When
        Optional<UserEntity> actualOptional = userRepository.findByLoginName("Karl");

        //Then
        assertTrue(actualOptional.isEmpty());
    }

    private UserEntity createAdminUser() {
        return userRepository.save(UserEntity.builder()
                .id(1L)
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("ADMIN").build());
    }
    private UserEntity createUser(){
        return userRepository.save(UserEntity.builder()
                .id(2L)
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("USER").build());
    }
}