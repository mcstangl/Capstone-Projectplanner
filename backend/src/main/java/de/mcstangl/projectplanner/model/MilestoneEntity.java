package de.mcstangl.projectplanner.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "milestone")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MilestoneEntity {

    @Id
    @GeneratedValue
    @Column(name ="milestone_id", unique = true,nullable = false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="date_finished")
    private Date dateFinished;

    @Column(name="due_date")
    private Date dueDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="project_id", nullable = false)
    private ProjectEntity projectEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MilestoneEntity that = (MilestoneEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
