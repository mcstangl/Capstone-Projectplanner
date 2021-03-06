package de.mcstangl.projectplanner.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredentialsDto {

    private final String loginName;
    private final String password;
}
