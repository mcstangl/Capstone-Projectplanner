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
           UserEntity updatedUserEntity = userService.updateUser(loginName, mapUser(userDto));
           return ok(mapUser(updatedUserEntity));
       }

       return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals(UserRole.ADMIN);
    }
}
