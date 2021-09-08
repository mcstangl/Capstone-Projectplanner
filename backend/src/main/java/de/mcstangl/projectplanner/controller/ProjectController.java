package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.Project;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Project> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody Project newProject){

        ProjectEntity newProjectEntity = projectService.createNewProject(map(newProject));

        return ok(map(newProjectEntity));
    }

    @GetMapping
    public ResponseEntity<List<Project>> findAll(){

        List<ProjectEntity> projectEntityList = projectService.findAll();

        return ok(map(projectEntityList));
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

    private List<Project> map(List<ProjectEntity> projectEntityList){
        List<Project> projectList = new LinkedList<>();
        for (ProjectEntity projectEntity : projectEntityList) {
            Project project = map(projectEntity);
            projectList.add(project);
        }
        return projectList;
    }
}
