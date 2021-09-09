package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.Project;
import de.mcstangl.projectplanner.api.UpdateProject;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.security.auth.message.AuthException;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/project-planner/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody Project newProject) {
        if (isAdmin(authUser)) {
            ProjectEntity newProjectEntity = projectService.createNewProject(map(newProject));
            return ok(map(newProjectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<Project>> findAll() {

        List<ProjectEntity> projectEntityList = projectService.findAll();

        return ok(map(projectEntityList));
    }

    @GetMapping("{title}")
    public ResponseEntity<Project> findByTitle(@PathVariable String title) {
        ProjectEntity projectEntity = projectService.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", title)));
        return ok(map(projectEntity));

    }

    @PutMapping("{title}")
    public ResponseEntity<Project> updateProject(@AuthenticationPrincipal UserEntity authUser, @PathVariable String title, @RequestBody UpdateProject updateProject) {

        if (!title.equals(updateProject.getTitle())) {
            throw new IllegalArgumentException();
        }

        if (isAdmin(authUser)) {

            String newTitle = updateProject.getNewTitle();

            ProjectEntity projectEntity = projectService.update(map(updateProject), newTitle);
            return ok(map(projectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

    private ProjectEntity map(UpdateProject updateProject) {
        return ProjectEntity.builder()
                .customer(updateProject.getCustomer())
                .title(updateProject.getTitle())
                .build();
    }

    private ProjectEntity map(Project project) {
        return ProjectEntity.builder()
                .customer(project.getCustomer())
                .title(project.getTitle())
                .build();
    }

    private Project map(ProjectEntity projectEntity) {
        return Project.builder()
                .customer(projectEntity.getCustomer())
                .title(projectEntity.getTitle())
                .build();
    }

    private List<Project> map(List<ProjectEntity> projectEntityList) {
        List<Project> projectList = new LinkedList<>();
        for (ProjectEntity projectEntity : projectEntityList) {
            Project project = map(projectEntity);
            projectList.add(project);
        }
        return projectList;
    }
}
