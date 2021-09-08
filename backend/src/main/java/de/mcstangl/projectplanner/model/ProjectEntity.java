package de.mcstangl.projectplanner.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectEntity that = (ProjectEntity) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
