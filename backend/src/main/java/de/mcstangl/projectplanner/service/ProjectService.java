package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.enums.ProjectStatus;
import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.springframework.util.Assert.hasText;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final MilestoneService milestoneService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService, MilestoneService milestoneService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.milestoneService = milestoneService;
    }

    public Optional<ProjectEntity> findByTitle(String title) {

        Optional<ProjectEntity> fetchedProjectEntityOpt = projectRepository.findByTitle(title);
        if (fetchedProjectEntityOpt.isPresent()) {
            ProjectEntity fetchedProjectEntity = fetchedProjectEntityOpt.get();
            if (fetchedProjectEntity.getMilestones() == null) {
                return Optional.of(fetchedProjectEntity);
            }

            sortProjectMilestones(fetchedProjectEntity);
            return Optional.of(fetchedProjectEntity);
        }

        return Optional.empty();
    }


    public ProjectEntity createNewProject(ProjectEntity projectEntity) {

        hasText(projectEntity.getCustomer(), "Kundenname darf nicht leer sein");
        hasText(projectEntity.getTitle(), "Projekttitel darf nicht leer sein");

        Optional<ProjectEntity> projectEntityOptional = findByTitle(projectEntity.getTitle());

        if (projectEntityOptional.isPresent()) {
            throw new EntityExistsException("Ein Projekt mit diesem Name existiert schon");
        }
        projectEntity.setMilestones(List.of());
        projectEntity.setStatus(ProjectStatus.OPEN);
        return projectRepository.save(projectEntity);
    }

    public List<ProjectEntity> findAll() {
        List<ProjectEntity> sortedProjectsWithMilestones = getAllProjectsSortedByMilestoneDueDate();
        List<ProjectEntity> allProjects = projectRepository.findAll();

        List<ProjectEntity> allProjectsSorted = new LinkedList<>(sortedProjectsWithMilestones);

        for (ProjectEntity project : allProjects) {
            if (!allProjectsSorted.contains(project)) {
                allProjectsSorted.add(project);
            }
        }

        return allProjectsSorted;
    }

    public List<ProjectEntity> getAllProjectsSortedByMilestoneDueDate() {
        List<ProjectEntity> sortedProjects = milestoneService.getAllSortedByDueDate().stream()
                .map(MilestoneEntity::getProjectEntity)
                .distinct()
                .toList();
        for (ProjectEntity project : sortedProjects) {
            sortProjectMilestones(project);
        }
        return sortedProjects;
    }

    public ProjectEntity update(ProjectEntity projectUpdateData, String newTitle) {

        ProjectEntity fetchedProjectEntity = getProjectEntity(projectUpdateData.getTitle());

        ProjectEntity projectEntityCopy = copyProjectEntity(fetchedProjectEntity);

        if (projectUpdateData.getCustomer() != null) {
            updateCustomer(projectUpdateData, projectEntityCopy);
        }

        if (projectUpdateData.getOwner() != null) {
            projectEntityCopy.setOwner(projectUpdateData.getOwner());
        }

        if(projectUpdateData.getStatus() != null){
            projectEntityCopy.setStatus(projectUpdateData.getStatus());
        } else projectEntityCopy.setStatus(ProjectStatus.OPEN);

        if (projectUpdateData.getDateOfReceipt() != null) {
            projectEntityCopy.setDateOfReceipt(projectUpdateData.getDateOfReceipt());
        }

        if (projectUpdateData.getWriters() != null) {
            updateWriters(projectUpdateData, projectEntityCopy);
        }

        if (projectUpdateData.getMotionDesigners() != null) {
            updateMotionDesigners(projectUpdateData, projectEntityCopy);
        }

        if (newTitle != null && !newTitle.trim().equals(fetchedProjectEntity.getTitle())) {
            projectEntityCopy.setTitle(newTitle);
        }
        return projectRepository.save(projectEntityCopy);
    }

    public ProjectEntity moveToArchive(String title) {
        ProjectEntity fetchProjectEntity = getProjectEntity(title);
        fetchProjectEntity.setStatus(ProjectStatus.ARCHIVE);
        return projectRepository.save(fetchProjectEntity);
    }

    public ProjectEntity restoreFromArchive(String title) {
        ProjectEntity fetchProjectEntity = getProjectEntity(title);
        fetchProjectEntity.setStatus(ProjectStatus.OPEN);
        return projectRepository.save(fetchProjectEntity);
    }

    private ProjectEntity getProjectEntity(String title) {
        return findByTitle(title).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format(
                                "Projekt mit dem Titel %s konnte nicht gefunden werden",
                                title))
        );
    }

    private void updateCustomer(ProjectEntity projectUpdateEntity, ProjectEntity projectEntityCopy) {
        hasText(projectUpdateEntity.getCustomer(), "Kundenname darf nicht leer sein");
        projectEntityCopy.setCustomer(projectUpdateEntity.getCustomer().trim());
    }

    private void updateWriters(ProjectEntity projectUpdateEntity, ProjectEntity projectEntityCopy) {
        Set<UserEntity> writersToUpdate = projectUpdateEntity.getWriters();
        projectEntityCopy.setWriters(new HashSet<>());
        for (UserEntity writer : writersToUpdate) {
            UserEntity writerToAdd = userService.findByLoginName(writer.getLoginName())
                    .orElseThrow(() -> new EntityNotFoundException("Der Benutzer konnte nicht gefunden werden"));
            projectEntityCopy.addWriter(writerToAdd);
        }
    }


    private void updateMotionDesigners(ProjectEntity projectUpdateEntity, ProjectEntity projectEntityCopy) {
        Set<UserEntity> motionDesignersToUpdate = projectUpdateEntity.getMotionDesigners();
        projectEntityCopy.setMotionDesigners(new HashSet<>());

        for (UserEntity motionDesigner : motionDesignersToUpdate) {
            UserEntity motionDesignerToAdd = userService.findByLoginName(motionDesigner.getLoginName())
                    .orElseThrow(() -> new EntityNotFoundException("Der Benutzer konnte nicht gefunden werden"));
            projectEntityCopy.addMotionDesigner(motionDesignerToAdd);
        }
    }


    private ProjectEntity copyProjectEntity(ProjectEntity fetchedProjectEntity) {
        return ProjectEntity.builder()
                .id(fetchedProjectEntity.getId())
                .customer(fetchedProjectEntity.getCustomer())
                .dateOfReceipt(fetchedProjectEntity.getDateOfReceipt())
                .title(fetchedProjectEntity.getTitle())
                .writers(fetchedProjectEntity.getWriters())
                .motionDesigners(fetchedProjectEntity.getMotionDesigners())
                .milestones(fetchedProjectEntity.getMilestones())
                .owner(fetchedProjectEntity.getOwner()).build();
    }


    private void sortProjectMilestones(ProjectEntity fetchedProjectEntity) {
        List<MilestoneEntity> milestoneEntityList = fetchedProjectEntity.getMilestones();
        List<MilestoneEntity> sortedMilestoneEntityList = sortMilestonesByDueDate(milestoneEntityList);
        fetchedProjectEntity.setMilestones(sortedMilestoneEntityList);
    }

    private List<MilestoneEntity> sortMilestonesByDueDate(List<MilestoneEntity> milestoneEntityList) {
        return milestoneEntityList.stream().sorted(Comparator.comparing(MilestoneEntity::getDueDate)).toList();
    }


}