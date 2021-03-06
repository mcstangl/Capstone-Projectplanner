package de.mcstangl.projectplanner.model;

import de.mcstangl.projectplanner.enums.ProjectStatus;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectEntity {

    @Id
    @GeneratedValue
    @Column(name = "project_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "customer")
    private String customer;

    @Column(name = "date_of_receipt", nullable = false)
    private Date dateOfReceipt;

    @Enumerated(STRING)
    @Column(name = "status")
    private ProjectStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_writers",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> writers;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_motion_designers",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> motionDesigners;

    @OneToMany(mappedBy = "projectEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MilestoneEntity> milestones;

    public void addWriter(UserEntity userEntity) {
        writers.add(userEntity);
    }

    public void addMotionDesigner(UserEntity userEntity) {
        motionDesigners.add(userEntity);
    }

    public void removeMilestone(MilestoneEntity milestoneEntity) {
        milestones.remove(milestoneEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectEntity that = (ProjectEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

}
