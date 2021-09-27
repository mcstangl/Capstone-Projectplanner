package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.Assert.hasText;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;

    }

    public Optional<UserEntity> findByLoginName(String loginName) {
        log.info(String.format("Fetched user %s", loginName));
        return userRepository.findByLoginName(loginName);
    }

    public List<UserEntity> findAll() {
        log.info("Fetched all users");
        return userRepository.findAll();
    }

    public UserEntity createNewUser(UserEntity newUserEntity) {

        hasText(newUserEntity.getLoginName(), "Login Name darf nicht leer sein");
        if (newUserEntity.getRole() == null) {
            throw new IllegalArgumentException("Die User Rolle darf nicht leer sein");
        }

        Optional<UserEntity> userEntityOpt = findByLoginName(newUserEntity.getLoginName());
        if (userEntityOpt.isPresent()) {
            throw new EntityExistsException("Ein User mit diesem Namen existiert schon");
        }
        log.info(String.format("User %s created", newUserEntity.getLoginName()));
        return saveUserEntityWithNewRandomPassword(newUserEntity);
    }


    public UserEntity updateUser(String loginName, UserEntity userUpdateData) {

        hasText(userUpdateData.getLoginName(), "Login Name darf nicht leer sein");

        if(!loginName.equals(userUpdateData.getLoginName())){
            Optional<UserEntity> userEntityOpt = findByLoginName(userUpdateData.getLoginName());
            if (userEntityOpt.isPresent()) {
                throw new EntityExistsException("Ein User mit diesem Namen existiert schon");
            }
        }

        UserEntity userEntity = getUserEntity(loginName);

        userEntity.setLoginName(userUpdateData.getLoginName());

        if (userUpdateData.getRole() != null) {
            userEntity.setRole(userUpdateData.getRole());
        }
        log.info(String.format("User %s updated", userEntity.getLoginName()));
        return userRepository.save(userEntity);
    }


    public UserEntity deleteUserByLoginName(String loginName) {
        UserEntity userEntity = getUserEntity(loginName);

        checkIfUserHasProjects(userEntity);

        userRepository.delete(userEntity);
        log.info(String.format("User %s deleted", loginName));
        return userEntity;
    }



    public UserEntity resetPassword(String loginName) {
        UserEntity fetchedUserEntity = getUserEntity(loginName);
        log.info(String.format("Reset password for user %s", loginName));
        return saveUserEntityWithNewRandomPassword(fetchedUserEntity);
    }

    public UserEntity updatePassword(String loginName, String password) {
        UserEntity userEntity = getUserEntity(loginName);
        hasText(password, "Das Passwort darf nicht leer sein");
        String hashedPassword = passwordService.getHashedPassword(password);
        userEntity.setPassword(hashedPassword);
        log.info(String.format("Updated password for user %s", loginName));

        return userRepository.save(userEntity);

    }

    private UserEntity saveUserEntityWithNewRandomPassword(UserEntity user) {
        String randomPassword = passwordService.getRandomPassword();
        String hashedPassword = passwordService.getHashedPassword(randomPassword);

        user.setPassword(hashedPassword);

        UserEntity savedUserEntity = userRepository.save(user);

        UserEntity userWithClearPassword = copyUserEntity(savedUserEntity);
        userWithClearPassword.setPassword(randomPassword);

        return userWithClearPassword;
    }


    private UserEntity getUserEntity(String loginName) {
        return findByLoginName(loginName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format("Der User %s konnte nicht gefunden werden", loginName)));
    }

    private void checkIfUserHasProjects(UserEntity userEntity) {
        Set<ProjectEntity> userProjects = new HashSet<>();
        if (userEntity.getOwnedProjects() != null){
            userProjects.addAll(userEntity.getOwnedProjects());
        }
        if (userEntity.getWriterInProjects() != null){
            userProjects.addAll(userEntity.getWriterInProjects());
        }
        if (userEntity.getMotionDesignerInProjects() != null){
            userProjects.addAll(userEntity.getMotionDesignerInProjects());
        }

        if(userProjects.size() > 0){
            throw new IllegalArgumentException(
                    String.format(
                            "Der Benutzer %s ist verknüpft in %s Projekten und kann nicht gelöscht werden",
                            userEntity.getLoginName(),
                            userProjects.size()));
        }
    }

    private UserEntity copyUserEntity(UserEntity userEntity) {
        return UserEntity.builder()
                .id(userEntity.getId())
                .loginName(userEntity.getLoginName())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();
    }



}
