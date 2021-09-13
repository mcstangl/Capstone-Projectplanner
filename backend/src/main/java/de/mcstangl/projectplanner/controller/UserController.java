package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(@AuthenticationPrincipal UserEntity authUser) {
        if(isAdmin(authUser)){
        List<UserEntity> userEntityList = userService.findAll();

        return ok(map(userEntityList));

        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private UserDto map(UserEntity userEntity) {
        return UserDto.builder()
                .loginName(userEntity.getLoginName())
                .role(userEntity.getRole())
                .build();
    }

    private List<UserDto> map(List<UserEntity> userEntityList){
        List<UserDto> userDtoList = new LinkedList<>();
        for (UserEntity userEntity : userEntityList) {
            userDtoList.add(map(userEntity));
        }
        return userDtoList;
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }
}
