package de.mcstangl.projectplanner.service;

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

    public ProjectEntity update(ProjectEntity projectUpdateEntity, String newTitle) {

        ProjectEntity fetchedProjectEntity = findByTitle(projectUpdateEntity.getTitle())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", projectUpdateEntity.getTitle())));

        ProjectEntity projectEntityCopy = copyProjectEntity(fetchedProjectEntity);

        if (projectUpdateEntity.getCustomer() != null) {
            hasText(projectUpdateEntity.getCustomer(), "Kundenname darf nicht leer sein");
            projectEntityCopy.setCustomer(projectUpdateEntity.getCustomer().trim());
        }

        if (projectUpdateEntity.getOwner() != null && !projectEntityCopy.getOwner().getLoginName().equals(projectUpdateEntity.getOwner().getLoginName())) {
            projectEntityCopy.setOwner(projectUpdateEntity.getOwner());
        }

        if (projectUpdateEntity.getWriters() != null) {
            Set<UserEntity> writersToUpdate = projectUpdateEntity.getWriters();
            Set<UserEntity> updatedWriters = projectEntityCopy.getWriters();
            if(updatedWriters == null){
                updatedWriters = new HashSet<>();
            }
            for (UserEntity writer : writersToUpdate) {
                UserEntity writerToAdd = userService.findByLoginName(writer.getLoginName())
                        .orElseThrow(() -> new EntityNotFoundException("Der Benutzer konnte nicht gefunden werden"));
                updatedWriters.add(writerToAdd);
            }
            projectEntityCopy.setWriters(updatedWriters);
        }

        if (projectUpdateEntity.getMotionDesigners() != null) {
            Set<UserEntity> motionDesignersToUpdate = projectUpdateEntity.getMotionDesigners();
            Set<UserEntity> updatedMotionDesigners = projectEntityCopy.getMotionDesigners();
            if(updatedMotionDesigners == null){
                updatedMotionDesigners = new HashSet<>();
            }
            for (UserEntity motionDesigner : motionDesignersToUpdate) {
                UserEntity motionDesignerToAdd = userService.findByLoginName(motionDesigner.getLoginName())
                        .orElseThrow(() -> new EntityNotFoundException("Der Benutzer konnte nicht gefunden werden"));
                updatedMotionDesigners.add(motionDesignerToAdd);
            }
            projectEntityCopy.setMotionDesigners(updatedMotionDesigners);
        }

        if (newTitle != null && !newTitle.trim().equals(fetchedProjectEntity.getTitle())) {
            hasText(newTitle, "Projekttitel darf nicht leer sein");
            projectEntityCopy.setTitle(newTitle.trim());
            projectEntityCopy.setId(null);
            projectRepository.delete(fetchedProjectEntity);
            return createNewProject(projectEntityCopy);
        }
        return projectRepository.save(projectEntityCopy);
    }

    private ProjectEntity copyProjectEntity(ProjectEntity fetchedProjectEntity) {
        return ProjectEntity.builder()
                .id(fetchedProjectEntity.getId())
                .customer(fetchedProjectEntity.getCustomer())
                .title(fetchedProjectEntity.getTitle())
                .owner(fetchedProjectEntity.getOwner()).build();
    }
}
