package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectService projectService;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, ProjectService projectService) {
        this.milestoneRepository = milestoneRepository;
        this.projectService = projectService;
    }

    public MilestoneEntity createNewMileStone(MilestoneEntity mileStone) {
        return milestoneRepository.save(mileStone);
    }

    public List<MilestoneEntity> findAllByProjectTitle(String projectTitle){
        ProjectEntity projectEntity = projectService
                .findByTitle(projectTitle)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Das Projekt mit dem Titel %s konnte nicht gefunden werden", projectTitle)));

        return milestoneRepository.findAllByProjectEntity(projectEntity);
    }
}
