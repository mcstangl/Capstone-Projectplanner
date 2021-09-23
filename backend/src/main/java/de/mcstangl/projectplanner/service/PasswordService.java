package de.mcstangl.projectplanner.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String getRandomPassword(){
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public String getHashedPassword(String password){
        return passwordEncoder.encode(password);
    }
}
