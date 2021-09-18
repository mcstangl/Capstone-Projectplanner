package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
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

    public MilestoneEntity createNewMilestone(MilestoneEntity newMilestone) {
        if (newMilestone.getId() != null) {
            throw new IllegalArgumentException("Ein neuer Milestone darf keine ID haben");
        }
        checkForExistingMilestoneForProject(newMilestone);
        return milestoneRepository.save(newMilestone);
    }


    public List<MilestoneEntity> findAllByProjectTitle(String projectTitle) {
        ProjectEntity projectEntity = projectService
                .findByTitle(projectTitle)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Das Projekt mit dem Titel %s konnte nicht gefunden werden", projectTitle)));

        return milestoneRepository.findAllByProjectEntity(projectEntity);
    }

    public MilestoneEntity updateMilestone(MilestoneEntity milestoneUpdateData) {
        return milestoneRepository.save(milestoneUpdateData);
    }

    private void checkForExistingMilestoneForProject(MilestoneEntity newMilestone) {
        List<MilestoneEntity> fetchedMilestonesForProject = findAllByProjectTitle(newMilestone.getProjectEntity().getTitle());
        for (MilestoneEntity fetchedMilestone : fetchedMilestonesForProject) {
            if (fetchedMilestone.getTitle().equals(newMilestone.getTitle())) {
                throw new EntityExistsException(String.format("Dieses Projekt hat bereits einen Milestone %s", newMilestone.getTitle()));
            }
        }
    }

    public MilestoneEntity deleteById(Long id) {

        MilestoneEntity milestoneEntity = milestoneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Milestone mit ID %s konnte nicht gefunden werden", id)));

        return projectService.removeMilestone(milestoneEntity);
    }
}
