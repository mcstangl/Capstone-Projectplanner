package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.Project;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.badRequest;
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
    public ResponseEntity<Project> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody Project newProject){

        ProjectEntity newProjectEntity = projectService.createNewProject(map(newProject));

        return ok(map(newProjectEntity));
    }


    private ProjectEntity map(Project project){
        return ProjectEntity.builder()
                .customer(project.getCustomer())
                .title(project.getTitle())
                .build();
    }

    private Project map(ProjectEntity projectEntity){
        return Project.builder()
                .customer(projectEntity.getCustomer())
                .title(projectEntity.getTitle())
                .build();
    }
}
