package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.AccessToken;
import de.mcstangl.projectplanner.api.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/project-planner/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
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
            return ok(AccessToken.builder().token("someToken").build());

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean validateCredentials(Credentials credentials) {
        return credentials == null ||
                credentials.getLoginName() == null ||
                credentials.getLoginName().isBlank() ||
                credentials.getPassword() == null ||
                credentials.getPassword().isBlank();
    }
}
