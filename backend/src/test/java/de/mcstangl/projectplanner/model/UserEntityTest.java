package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.SpringBootTests;
import de.mcstangl.projectplanner.repository.UserRepository;
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


    @BeforeEach
    public void setup(){
        UserEntity admin = UserEntity.builder()
                .loginName("Hans")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("ADMIN").build();

        UserEntity user = UserEntity.builder()
                .loginName("Dave")
                .password("$2a$10$wFun/giZHIbz7.qC2Kv97.uPgNGYOqRUW62d2m5NobVAJZLA3gZA.")
                .role("USER").build();
        userRepository.saveAndFlush(admin);
        userRepository.saveAndFlush(user);
    }

    @Test
    @Transactional
    @DisplayName("Find user by loginName should return the user found")
    public void findUserByLoginName(){
        //When
        Optional<UserEntity> actualOptional = userRepository.findByLoginName("Hans");

        //Then
        assertTrue(actualOptional.isPresent());
        assertThat(actualOptional.get().getLoginName(), is("Hans"));
    }
}