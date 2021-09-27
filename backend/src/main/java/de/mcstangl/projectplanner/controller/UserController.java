package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.api.UserWithPasswordDto;
import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/user")
public class UserController extends Mapper {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(@AuthenticationPrincipal UserEntity authUser) {
        if(isAdmin(authUser)){
        List<UserEntity> userEntityList = userService.findAll();

        return ok(mapUser(userEntityList));

        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping
    public ResponseEntity<UserDto> createNewUser(@AuthenticationPrincipal UserEntity authUser, @RequestBody UserDto newUserDto){
        if(isAdmin(authUser)){
           UserEntity createdUserEntity = userService.createNewUser(mapUser(newUserDto));
            UserWithPasswordDto userWithPasswordDto = UserWithPasswordDto.builder()
                    .loginName(createdUserEntity.getLoginName())
                    .password(createdUserEntity.getPassword())
                    .role(createdUserEntity.getRole().toString())
                    .build();
           return ok(userWithPasswordDto);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("{loginName}")
    public ResponseEntity<UserDto> findByLoginName(@PathVariable String loginName){

        UserEntity userEntity = userService.findByLoginName(loginName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format("Der User %s konnte nicht gefunden werden", loginName)));

        return ok(mapUser(userEntity));
    }


    @PutMapping("{loginName}")
    public ResponseEntity<UserDto> updateUser(@AuthenticationPrincipal UserEntity authUser, @PathVariable String loginName, @RequestBody UserDto userDto){
        if(isAdmin(authUser)) {
            UserEntity updatedUserEntity = userService.updateUser(loginName, mapUser(userDto));
            return ok(mapUser(updatedUserEntity));
        }
       if(!isAdmin(authUser) && authUser.getLoginName().equals(loginName)){
           userDto.setRole("USER");
           UserEntity updatedUserEntity = userService.updateUser(loginName, mapUser(userDto));
           return ok(mapUser(updatedUserEntity));
       }

       return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("{loginName}/reset-password")
    public ResponseEntity<UserWithPasswordDto> resetUserPassword(@AuthenticationPrincipal UserEntity authUser, @PathVariable String loginName){
        if(isAdmin(authUser)){
           UserEntity userEntity = userService.resetPassword(loginName);
           UserWithPasswordDto userWithPasswordDto = UserWithPasswordDto.builder()
                   .loginName(userEntity.getLoginName())
                   .role(userEntity.getRole().toString())
                   .password(userEntity.getPassword())
                   .build();
           return ok(userWithPasswordDto);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("{loginName}")
    public ResponseEntity<UserDto> delete(@AuthenticationPrincipal UserEntity authUser, @PathVariable String loginName){
        if(isAdmin(authUser) && authUser.getLoginName().equals(loginName)){
            throw new IllegalArgumentException("Ein Admin darf sich nicht selbst löschen");
        }

        if(isAdmin(authUser)){
            UserEntity deletedUser = userService.deleteUserByLoginName(loginName);
            return ok(mapUser(deletedUser));
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals(UserRole.ADMIN);
    }
}
