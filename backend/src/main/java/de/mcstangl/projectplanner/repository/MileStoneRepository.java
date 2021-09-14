package de.mcstangl.projectplanner.repository;

import de.mcstangl.projectplanner.model.MileStoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MileStoneRepository extends JpaRepository<MileStoneEntity, Long> {

    List<MileStoneEntity> findAllByProjectEntity(ProjectEntity projectEntity);
}
