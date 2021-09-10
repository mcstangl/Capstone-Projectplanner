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
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody ProjectDto newProject) {
        if (isAdmin(authUser)) {

            UserEntity ownerEntity = userService.findByLoginName(newProject.getOwnerName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", newProject.getOwnerName())));

            ProjectEntity newProjectEntity = map(newProject);

            newProjectEntity.setOwner(ownerEntity);

            ProjectEntity createdProjectEntity = projectService.createNewProject(newProjectEntity);

            return ok(map(createdProjectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> findAll() {

        List<ProjectEntity> projectEntityList = projectService.findAll();

        return ok(map(projectEntityList));
    }

    @GetMapping("{title}")
    public ResponseEntity<ProjectDto> findByTitle(@PathVariable String title) {
        ProjectEntity projectEntity = projectService.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", title)));
        return ok(map(projectEntity));

    }

    @PutMapping("{title}")
    public ResponseEntity<ProjectDto> updateProject(@AuthenticationPrincipal UserEntity authUser, @PathVariable String title, @RequestBody UpdateProjectDto updateProjectDto) {

        if (!title.equals(updateProjectDto.getTitle())) {
            throw new IllegalArgumentException();
        }

        if (isAdmin(authUser)) {

            String newTitle = updateProjectDto.getNewTitle();

            ProjectEntity projectEntity = projectService.update(map(updateProjectDto), newTitle);
            return ok(map(projectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

    private ProjectEntity map(UpdateProjectDto updateProjectDto) {
        return ProjectEntity.builder()
                .customer(updateProjectDto.getCustomer())
                .title(updateProjectDto.getTitle())
                .build();
    }

    private ProjectEntity map(ProjectDto projectDto) {
        return ProjectEntity.builder()
                .customer(projectDto.getCustomer())
                .title(projectDto.getTitle())
                .build();
    }

    private ProjectDto map(ProjectEntity projectEntity) {
        return ProjectDto.builder()
                .customer(projectEntity.getCustomer())
                .ownerName(projectEntity.getOwner().getLoginName())
                .title(projectEntity.getTitle())
                .build();
    }

    private List<ProjectDto> map(List<ProjectEntity> projectEntityList) {
        List<ProjectDto> projectDtoList = new LinkedList<>();
        for (ProjectEntity projectEntity : projectEntityList) {
            projectDtoList.add(map(projectEntity));
        }
        return projectDtoList;
    }
}
