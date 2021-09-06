package de.mcstangl.projectplanner.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String loginName;
    private String role;
}
