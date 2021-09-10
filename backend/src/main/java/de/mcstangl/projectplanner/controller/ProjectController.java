package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
    public ResponseEntity<ProjectDto> createNewProject(@AuthenticationPrincipal UserEntity authUser, @RequestBody ProjectDto newProject) {
        if (isAdmin(authUser)) {
            ProjectEntity newProjectEntity = projectService.createNewProject(map(newProject));
            return ok(map(newProjectEntity));
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
                .title(projectEntity.getTitle())
                .build();
    }

    private List<ProjectDto> map(List<ProjectEntity> projectEntityList) {
        List<ProjectDto> projectDtoList = new LinkedList<>();
        for (ProjectEntity projectEntity : projectEntityList) {
            ProjectDto projectDto = map(projectEntity);
            projectDtoList.add(projectDto);
        }
        return projectDtoList;
    }
}
