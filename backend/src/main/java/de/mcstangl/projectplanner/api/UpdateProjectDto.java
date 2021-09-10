package de.mcstangl.projectplanner.api;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateProjectDto extends ProjectDto {


    private String newTitle;


}


