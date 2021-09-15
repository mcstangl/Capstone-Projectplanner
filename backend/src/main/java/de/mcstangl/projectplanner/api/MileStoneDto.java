package de.mcstangl.projectplanner.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MileStoneDto {

    private Long id;
    private String title;
    private String dateFinished;
    private String dueDate;
    private String projectTitle;

}
