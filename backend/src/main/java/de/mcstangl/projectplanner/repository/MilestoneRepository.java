package de.mcstangl.projectplanner.repository;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<MilestoneEntity, Long> {

    List<MilestoneEntity> findAllByProjectEntity(ProjectEntity projectEntity);
}
