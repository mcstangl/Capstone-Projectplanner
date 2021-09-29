package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.DefaultMilestone;
import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MilestoneRepository;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.*;

@Service
@Slf4j
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final DateService dateService;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, ProjectRepository projectRepository, DateService dateService) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.dateService = dateService;
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

        checkForExistingMilestoneForProject(milestoneUpdateData);

        log.info(String.format("Updated milestone %s for project %s", milestoneUpdateData.getTitle(), milestoneUpdateData.getProjectEntity().getTitle()));
        return milestoneRepository.save(milestoneUpdateData);
    }

    private void checkForExistingMilestoneForProject(MilestoneEntity newMilestone) {
        List<MilestoneEntity> fetchedMilestonesForProject = findAllByProjectTitle(newMilestone.getProjectEntity().getTitle());
        for (MilestoneEntity fetchedMilestone : fetchedMilestonesForProject) {
            if (fetchedMilestone.getTitle().equals(newMilestone.getTitle()) && !fetchedMilestone.getId().equals(newMilestone.getId()) ) {
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

    public List<MilestoneEntity> getDefaultMilestones(Date dateOfReceipt, ProjectEntity projectEntity) {
        Date firstDate = dateService.addBusinessDays(dateOfReceipt, DefaultMilestone.TEXTERSTELLUNG.getDueTime());
        MilestoneEntity firstMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.TEXTERSTELLUNG.title)
                .projectEntity(projectEntity)
                .dueDate(firstDate)
                .build();

        Date secondDate = dateService.addBusinessDays(firstDate, DefaultMilestone.REDAKTIONSFREIGABE.getDueTime());
        MilestoneEntity secondMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.REDAKTIONSFREIGABE.title)
                .projectEntity(projectEntity)
                .dueDate(secondDate).build();

        Date thirdDate = dateService.addBusinessDays(secondDate, DefaultMilestone.KUNDENKORREKTUR.getDueTime());
        MilestoneEntity thirdMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.KUNDENKORREKTUR.title)
                .projectEntity(projectEntity)
                .dueDate(thirdDate).build();

        Date forthDate = dateService.addBusinessDays(thirdDate, DefaultMilestone.MOTIONGRAFIK.getDueTime());
        MilestoneEntity forthMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.MOTIONGRAFIK.title)
                .projectEntity(projectEntity)
                .dueDate(forthDate).build();

        Date fifthDate = dateService.addBusinessDays(forthDate, DefaultMilestone.GRAFIK.getDueTime());
        MilestoneEntity fifthMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.GRAFIK.title)
                .projectEntity(projectEntity)
                .dueDate(fifthDate).build();

        Date sixthDate = dateService.addBusinessDays(fifthDate, DefaultMilestone.ABNAHME.getDueTime());
        MilestoneEntity sixthMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.ABNAHME.title)
                .projectEntity(projectEntity)
                .dueDate(sixthDate).build();

        Date seventhDate = dateService.addBusinessDays(sixthDate, DefaultMilestone.INHOUSE.getDueTime());
        MilestoneEntity seventhMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.INHOUSE.title)
                .projectEntity(projectEntity)
                .dueDate(seventhDate).build();

        Date eightDate = dateService.addBusinessDays(seventhDate, DefaultMilestone.EINSPIELUNG.getDueTime());
        MilestoneEntity eightMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.EINSPIELUNG.title)
                .projectEntity(projectEntity)
                .dueDate(eightDate).build();

        Date ninthDate = dateService.addBusinessDays(eightDate, DefaultMilestone.HAUSINTERNERPROZESS.getDueTime());
        MilestoneEntity ninthMilestone = MilestoneEntity.builder()
                .title(DefaultMilestone.HAUSINTERNERPROZESS.title)
                .projectEntity(projectEntity)
                .dueDate(ninthDate).build();


        List<MilestoneEntity> defaultMilestones = new LinkedList<>();
        defaultMilestones.add(firstMilestone);
        defaultMilestones.add(secondMilestone);
        defaultMilestones.add(thirdMilestone);
        defaultMilestones.add(forthMilestone);
        defaultMilestones.add(fifthMilestone);
        defaultMilestones.add(sixthMilestone);
        defaultMilestones.add(seventhMilestone);
        defaultMilestones.add(eightMilestone);
        defaultMilestones.add(ninthMilestone);

        return defaultMilestones;
    }

    public List<MilestoneEntity> sortMilestonesByDueDate(List<MilestoneEntity> milestoneEntityList) {
        return milestoneEntityList.stream().sorted(Comparator.comparing(MilestoneEntity::getDueDate)).toList();
    }


}
