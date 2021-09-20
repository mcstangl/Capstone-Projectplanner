package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
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
            throw new IllegalArgumentException("Ein neuer Milestone darf keine ID haben");
        }
        checkForExistingMilestoneForProject(newMilestone);
        return milestoneRepository.save(newMilestone);
    }

    public List<MilestoneEntity> findAll() {
        return milestoneRepository.findAll();
    }

    public List<MilestoneEntity> getAllSortedByDueDate() {
        List<MilestoneEntity> milestoneEntityList = findAll();
        return sortMilestonesByDueDate(milestoneEntityList);
    }

    public List<MilestoneEntity> findAllByProjectTitle(String projectTitle) {
        ProjectEntity projectEntity = projectRepository
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

        Optional<ProjectEntity> projectEntityOptional = projectRepository.findByTitle(milestoneEntity.getProjectEntity().getTitle());

        ProjectEntity fetchedProjectEntity = projectEntityOptional.orElseThrow(() -> new EntityNotFoundException(
                String.format(
                        "Projekt mit dem Titel %s konnte nicht gefunden werden",
                        milestoneEntity.getProjectEntity().getTitle()
                )
        ));

        List<MilestoneEntity> updatedMilestoneEntityList = new LinkedList<>();

        for (MilestoneEntity milestone : fetchedProjectEntity.getMilestones()) {
            if (!milestone.equals(milestoneEntity)) {
                updatedMilestoneEntityList.add(milestone);
            }
        }

        fetchedProjectEntity.setMilestones(updatedMilestoneEntityList);
        projectRepository.save(fetchedProjectEntity);

        return milestoneEntity;


    }

    public List<MilestoneEntity> sortMilestonesByDueDate(List<MilestoneEntity> milestoneEntityList) {
        return milestoneEntityList.stream().sorted(Comparator.comparing(MilestoneEntity::getDueDate)).toList();
    }
}
