package de.mcstangl.projectplanner.api;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessToken {
    private final String token;
}
