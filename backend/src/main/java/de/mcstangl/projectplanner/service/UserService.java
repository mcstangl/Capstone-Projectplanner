package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> findByLoginName(String loginName) {
       return userRepository.findByLoginName(loginName);
    }

    public List<UserEntity> findAll() {
        return  userRepository.findAll();
    }
}
