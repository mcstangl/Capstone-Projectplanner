package de.mcstangl.projectplanner.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProject {

    private String customer;
    private String title;
    private String newTitle;

}


