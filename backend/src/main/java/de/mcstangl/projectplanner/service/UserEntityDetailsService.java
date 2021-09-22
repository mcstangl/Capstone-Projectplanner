package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserEntityDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserEntityDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        UserEntity userEntity = userService
                .findByLoginName(loginName)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user =" + loginName));
        return User.builder()
                .username(userEntity.getLoginName())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();
    }
}
