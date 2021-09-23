package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.Assert.hasText;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;

    }

    public Optional<UserEntity> findByLoginName(String loginName) {
       return userRepository.findByLoginName(loginName);
    }

    public List<UserEntity> findAll() {
        return  userRepository.findAll();
    }

    public UserEntity createNewUser(UserEntity newUserEntity) {

        hasText(newUserEntity.getLoginName(), "Login Name darf nicht leer sein");
        if(newUserEntity.getRole() == null){
            throw new IllegalArgumentException("Die User Rolle darf nicht leer sein");
        }

        Optional<UserEntity> userEntityOpt = findByLoginName(newUserEntity.getLoginName());
        if(userEntityOpt.isPresent()){
            throw new EntityExistsException("Ein User mit diesem Namen existiert schon");
        }
        String randomPassword = passwordService.getRandomPassword();
        String hashedPassword = passwordService.getHashedPassword(randomPassword);

        newUserEntity.setPassword(hashedPassword);

        UserEntity savedUserEntity = userRepository.save(newUserEntity);

        UserEntity userWithClearPassword = copyUserEntity(savedUserEntity);
        userWithClearPassword.setPassword(randomPassword);

        return userWithClearPassword;
    }


    private UserEntity copyUserEntity(UserEntity userEntity){
        return UserEntity.builder()
                .id(userEntity.getId())
                .loginName(userEntity.getLoginName())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();
    }

    public UserEntity updateUser(String loginName, UserEntity userUpdateData) {

        hasText(userUpdateData.getLoginName(), "Login Name darf nicht leer sein");
        UserEntity userEntity = findByLoginName(loginName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format("Der User %s konnte nicht gefunden werden", loginName)));

        userEntity.setLoginName(userUpdateData.getLoginName());

        if(userUpdateData.getRole() != null){
            userEntity.setRole(userUpdateData.getRole());
        }

        return userRepository.save(userEntity);
    }
}
