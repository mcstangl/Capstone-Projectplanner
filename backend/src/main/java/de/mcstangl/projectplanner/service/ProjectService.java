package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.Assert.hasText;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public Optional<ProjectEntity> findByTitle(String title) {
        return projectRepository.findByTitle(title);
    }

    public ProjectEntity createNewProject(ProjectEntity projectEntity) {

        hasText(projectEntity.getCustomer(), "Kundenname darf nicht leer sein");
        hasText(projectEntity.getTitle(), "Projekttitel darf nicht leer sein");

        Optional<ProjectEntity> projectEntityOptional = findByTitle(projectEntity.getTitle());

        if (projectEntityOptional.isPresent()) {
            throw new EntityExistsException("Ein Projekt mit diesem Name existiert schon");
        }
        return projectRepository.save(projectEntity);
    }

    public List<ProjectEntity> findAll() {
        return projectRepository.findAll();
    }

    public ProjectEntity update(ProjectEntity projectUpdateData, String newTitle) {

        ProjectEntity fetchedProjectEntity = fetchProjectEntity(projectUpdateData.getTitle());

        ProjectEntity projectEntityCopy = copyProjectEntity(fetchedProjectEntity);

        if (projectUpdateData.getCustomer() != null) {
            updateCustomer(projectUpdateData, projectEntityCopy);
        }

        if (projectUpdateData.getOwner() != null) {
            projectEntityCopy.setOwner(projectUpdateData.getOwner());
        }

        if(projectUpdateData.getDateOfReceipt() != null){
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

    protected MilestoneEntity removeMilestone(MilestoneEntity milestoneEntity) {

        ProjectEntity fetchedProjectEntity = fetchProjectEntity(milestoneEntity.getProjectEntity().getTitle());
        fetchedProjectEntity.getMilestones().remove(milestoneEntity);
        projectRepository.save(fetchedProjectEntity);
        return milestoneEntity;
    }


    private ProjectEntity fetchProjectEntity(String title) {
        return findByTitle(title)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format(
                                        "Projekt mit dem Titel %s konnte nicht gefunden werden",
                                        title
                                )
                        )
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

}
