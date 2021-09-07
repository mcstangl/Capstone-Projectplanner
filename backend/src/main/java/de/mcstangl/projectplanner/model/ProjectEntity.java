package de.mcstangl.projectplanner.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name ="projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue
    @Column(name="project_id", nullable = false, unique = true)
    private Long id;

    @Column(name="title", nullable = false, unique = true)
    private String title;

    @Column(name="customer")
    private String customer;

}
