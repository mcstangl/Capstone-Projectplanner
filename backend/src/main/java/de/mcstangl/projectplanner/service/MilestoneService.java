package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, ProjectRepository projectRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
    }

    public MilestoneEntity createNewMilestone(MilestoneEntity newMilestone) {
        if (newMilestone.getId() != null) {
            log.debug("Create milestone failed. New milestone already had an ID");
            throw new IllegalArgumentException("Ein neuer Milestone darf keine ID haben");
        }
        checkForExistingMilestoneForProject(newMilestone);
        String logMessage = String.format("New milestone %s for project %s created.", newMilestone.getTitle(), newMilestone.getProjectEntity().getTitle());
        log.info(logMessage);
        return milestoneRepository.save(newMilestone);
    }

    public List<MilestoneEntity> findAll() {
        log.info("Fetched all milestones from DB");
        return milestoneRepository.findAll();
    }

    public List<MilestoneEntity> getAllSortedByDueDate() {
        List<MilestoneEntity> milestoneEntityList = findAll().stream()
                .filter(milestoneEntity -> milestoneEntity.getDateFinished() == null).toList();
        log.info("Sorted all milestones by due date");
        return sortMilestonesByDueDate(milestoneEntityList);
    }

    public List<MilestoneEntity> findAllByProjectTitle(String projectTitle) {
        ProjectEntity projectEntity = projectRepository
                .findByTitle(projectTitle)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Das Projekt mit dem Titel %s konnte nicht gefunden werden", projectTitle)));
        log.info(String.format("Fetched all milestones for project %s", projectTitle));
        return milestoneRepository.findAllByProjectEntity(projectEntity);
    }

    public MilestoneEntity updateMilestone(MilestoneEntity milestoneUpdateData) {
        log.info(String.format("Updated milestone %s for project %s", milestoneUpdateData.getTitle(), milestoneUpdateData.getProjectEntity().getTitle()));
        return milestoneRepository.save(milestoneUpdateData);
    }

    private void checkForExistingMilestoneForProject(MilestoneEntity newMilestone) {
        List<MilestoneEntity> fetchedMilestonesForProject = findAllByProjectTitle(newMilestone.getProjectEntity().getTitle());
        for (MilestoneEntity fetchedMilestone : fetchedMilestonesForProject) {
            if (fetchedMilestone.getTitle().equals(newMilestone.getTitle())) {
                log.info(String.format("Check failed: Project %s already has a milestone %s", newMilestone.getProjectEntity().getTitle(), newMilestone.getTitle()));
                throw new EntityExistsException(String.format("Dieses Projekt hat bereits einen Milestone %s", newMilestone.getTitle()));
            }
        }
    }

    public MilestoneEntity deleteById(Long id) {

        MilestoneEntity milestoneEntity = milestoneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Milestone mit ID %s konnte nicht gefunden werden", id)));

        Optional<ProjectEntity> projectEntityOptional = projectRepository.findByTitle(milestoneEntity.getProjectEntity().getTitle());

        ProjectEntity fetchedProjectEntity = projectEntityOptional.orElseThrow(() -> new EntityNotFoundException(
                String.format(
                        "Projekt mit dem Titel %s konnte nicht gefunden werden",
                        milestoneEntity.getProjectEntity().getTitle()
                )
        ));

        fetchedProjectEntity.removeMilestone(milestoneEntity);
        projectRepository.save(fetchedProjectEntity);
        log.info(String.format("Milestone %s in project %s deleted", milestoneEntity.getTitle(), fetchedProjectEntity.getTitle()));
        return milestoneEntity;


    }

    public List<MilestoneEntity> sortMilestonesByDueDate(List<MilestoneEntity> milestoneEntityList) {
        return milestoneEntityList.stream().sorted(Comparator.comparing(MilestoneEntity::getDueDate)).toList();
    }
}
