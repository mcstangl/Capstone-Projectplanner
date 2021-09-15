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



    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }
}
