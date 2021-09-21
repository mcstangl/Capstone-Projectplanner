package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.ProjectService;
import de.mcstangl.projectplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/project")
public class ProjectController extends Mapper{

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody ProjectDto newProject) {
        
        validateDate(newProject.getDateOfReceipt());

        initializeWriterListIfNull(newProject);
        initializeMotionDesignerListIfNull(newProject);

        if (isAdmin(authUser)) {

            UserEntity ownerEntity = getOwnerEntity(newProject);
            ProjectEntity newProjectEntity = mapProject(newProject);

            newProjectEntity.setOwner(ownerEntity);

            ProjectEntity createdProjectEntity = projectService.createNewProject(newProjectEntity);
            return ok(mapProject(createdProjectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @GetMapping
    public ResponseEntity<List<ProjectDto>> findAll() {

        List<ProjectEntity> projectEntityList = projectService.findAll();

        return ok(mapProject(projectEntityList));
    }

    @GetMapping("{title}")
    public ResponseEntity<ProjectDto> findByTitle(@PathVariable String title) {
        ProjectEntity projectEntity = projectService.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", title)));
        return ok(mapProject(projectEntity));

    }

    @PutMapping("{title}")
    public ResponseEntity<ProjectDto> updateProject(@AuthenticationPrincipal UserEntity authUser, @PathVariable String title, @RequestBody UpdateProjectDto updateProjectDto) {

        if (!title.equals(updateProjectDto.getTitle())) {
            throw new IllegalArgumentException("Fehler in der Anfrage: Pfadvariable und Titel stimmen nicht überein");
        }

        validateDate(updateProjectDto.getDateOfReceipt());

        initializeWriterListIfNull(updateProjectDto);
        initializeMotionDesignerListIfNull(updateProjectDto);

        if (isAdmin(authUser)) {

            String newTitle = updateProjectDto.getNewTitle();
            UserEntity ownerEntity = getOwnerEntity(updateProjectDto);
            ProjectEntity projectUpdateEntity = mapProject(updateProjectDto);
            projectUpdateEntity.setOwner(ownerEntity);
            ProjectEntity updatedProjectEntity = projectService.update(projectUpdateEntity, newTitle);
            return ok(mapProject(updatedProjectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("{title}/archive")
    public ResponseEntity<ProjectDto> moveToArchive(@AuthenticationPrincipal UserEntity authUser, @PathVariable String title){
        if(isAdmin(authUser)){
            ProjectEntity projectEntity = projectService.moveToArchive(title);
            return ok(mapProject(projectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("{title}/restore")
    public ResponseEntity<ProjectDto> restoreFromArchive(@AuthenticationPrincipal UserEntity authUser, @PathVariable String title){
        if(isAdmin(authUser)){
            ProjectEntity projectEntity = projectService.restoreFromArchive(title);
            return ok(mapProject(projectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

    private void initializeMotionDesignerListIfNull(ProjectDto projectDto) {
        if(projectDto.getMotionDesign() == null){
            projectDto.setMotionDesign(List.of());
        }
    }

    private void initializeWriterListIfNull(ProjectDto projectDto) {
        if(projectDto.getWriter() == null){
            projectDto.setWriter(List.of());
        }
    }

    private void validateDate(String date) {
        try{
            Date.valueOf(date);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Bitte geben Sie ein gültiges Datum ein");
        }
    }

    private UserEntity getOwnerEntity(ProjectDto newProject) {
        checkThatOwnerIsNotNull(newProject);
        return userService.findByLoginName(newProject.getOwner().getLoginName())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", newProject.getOwner().getLoginName())));
    }
    private UserEntity getOwnerEntity(UpdateProjectDto updateProjectDto) {
        checkThatOwnerIsNotNull(updateProjectDto);
        return userService.findByLoginName(updateProjectDto.getOwner().getLoginName())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", updateProjectDto.getOwner().getLoginName())));
    }
    private void checkThatOwnerIsNotNull(ProjectDto projectDto) {
        if(projectDto.getOwner() == null){
            throw new IllegalArgumentException("Ein Projekt muss eine*n Projektleiter*in haben");
        }
    }

}
