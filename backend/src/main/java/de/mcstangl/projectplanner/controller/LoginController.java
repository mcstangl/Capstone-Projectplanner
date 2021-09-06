package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.AccessToken;
import de.mcstangl.projectplanner.api.Credentials;
import de.mcstangl.projectplanner.api.User;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.JwtService;
import de.mcstangl.projectplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("api/project-planner/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("access_token")
    public ResponseEntity<AccessToken> getAccessToken(@RequestBody Credentials credentials) {
        if (validateCredentials(credentials)) {
            return badRequest().build();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getLoginName(),
                credentials.getPassword()
        );
        try {
            authenticationManager.authenticate(authenticationToken);
            Optional<UserEntity> userEntityOptional = userService.findByLoginName(credentials.getLoginName());
            if(userEntityOptional.isEmpty()){
                return notFound().build();
            }
            String token = jwtService.createToken(userEntityOptional.get());
            return ok(AccessToken.builder().token(token).build());

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("me")
    public ResponseEntity<User> authMe(@AuthenticationPrincipal UserEntity authUser){
        return ok(User.builder().loginName(authUser.getLoginName()).role(authUser.getRole()).build());
    }

    private boolean validateCredentials(Credentials credentials) {
        return credentials == null ||
                credentials.getLoginName() == null ||
                credentials.getLoginName().isBlank() ||
                credentials.getPassword() == null ||
                credentials.getPassword().isBlank();
    }
}
