package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.AccessTokenDto;
import de.mcstangl.projectplanner.api.CredentialsDto;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.JwtService;
import de.mcstangl.projectplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.util.Assert.hasText;

@CrossOrigin
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
    public ResponseEntity<AccessTokenDto> getAccessToken(@RequestBody CredentialsDto credentialsDto) {

        hasText(credentialsDto.getLoginName(), "Bitte geben sie Ihren Benutzernamen ein.");
        hasText(credentialsDto.getPassword(), "Bitte geben sie Ihr Passwort ein.");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentialsDto.getLoginName(),
                credentialsDto.getPassword()
        );

        authenticationManager.authenticate(authenticationToken);

        UserEntity userEntity = userService.findByLoginName(credentialsDto.getLoginName())
                .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", credentialsDto.getLoginName())
                        )
                );

        String token = jwtService.createToken(userEntity);
        return ok(AccessTokenDto.builder().token(token).build());
    }

    @GetMapping("me")
    public ResponseEntity<UserDto> authMe(@AuthenticationPrincipal UserEntity authUser) {
        return ok(UserDto.builder().loginName(authUser.getLoginName()).role(authUser.getRole()).build());
    }

    private boolean validateCredentials(CredentialsDto credentialsDto) {
        return credentialsDto == null ||
                credentialsDto.getLoginName() == null ||
                credentialsDto.getLoginName().isBlank() ||
                credentialsDto.getPassword() == null ||
                credentialsDto.getPassword().isBlank();
    }
}
