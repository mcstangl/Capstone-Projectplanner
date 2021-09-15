package de.mcstangl.projectplanner.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id")
    private Set<UserEntity> writers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "motionDesinger_id")
    private Set<UserEntity> motionDesigners = new HashSet<>();

    @OneToMany(mappedBy = "projectEntity" , fetch=FetchType.EAGER )
    private Set<MilestoneEntity> milestones;

    public void addWriter(UserEntity userEntity) {
        writers.add(userEntity);
    }

    public void addMotionDesigner(UserEntity userEntity) {
        motionDesigners.add(userEntity);
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
