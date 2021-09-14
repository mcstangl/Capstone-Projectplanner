package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.api.UserDto;
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
import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        if(newProject.getOwner() == null){
            throw new IllegalArgumentException("Ein Projekt muss eine*n Projektleiter*in haben");
        }
        if(newProject.getWriter() == null){
            newProject.setWriter(List.of());
        }
        if(newProject.getMotionDesign() == null){
            newProject.setMotionDesign(List.of());
        }
        if (isAdmin(authUser)) {


            UserEntity ownerEntity = getOwnerEntity(newProject);

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
            throw new IllegalArgumentException("Fehler in der Anfrage: Pfadvariable und Titel stimmen nicht überein");
        }
        if(updateProjectDto.getWriter() == null){
            updateProjectDto.setWriter(List.of());
        }
        if(updateProjectDto.getMotionDesign() == null){
            updateProjectDto.setMotionDesign(List.of());
        }


        if (isAdmin(authUser)) {

            String newTitle = updateProjectDto.getNewTitle();
            UserEntity ownerEntity = getOwnerEntity(updateProjectDto);
            ProjectEntity projectUpdateEntity = map(updateProjectDto);
            projectUpdateEntity.setOwner(ownerEntity);
            ProjectEntity updatedProjectEntity = projectService.update(projectUpdateEntity, newTitle);
            return ok(map(updatedProjectEntity));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

    private UserEntity getOwnerEntity(ProjectDto newProject) {
        return userService.findByLoginName(newProject.getOwner().getLoginName())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", newProject.getOwner().getLoginName())));
    }
    private UserEntity getOwnerEntity(UpdateProjectDto updateProjectDto) {
        return userService.findByLoginName(updateProjectDto.getOwner().getLoginName())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Benutzer mit dem Namen %s konnte nicht gefunden werden", updateProjectDto.getOwner().getLoginName())));
    }
    private ProjectEntity map(UpdateProjectDto updateProjectDto) {
        return ProjectEntity.builder()
                .owner(map(updateProjectDto.getOwner()))
                .customer(updateProjectDto.getCustomer())
                .dateOfReceipt(Date.valueOf(updateProjectDto.getDateOfReceipt()))
                .writers(mapUserDto(updateProjectDto.getWriter()))
                .motionDesigners(mapUserDto(updateProjectDto.getMotionDesign()))
                .title(updateProjectDto.getTitle())
                .build();
    }


    private ProjectEntity map(ProjectDto projectDto) {
        return ProjectEntity.builder()
                .customer(projectDto.getCustomer())
                .title(projectDto.getTitle())
                .dateOfReceipt(Date.valueOf(projectDto.getDateOfReceipt()))
                .writers(mapUserDto(projectDto.getWriter()))
                .motionDesigners(mapUserDto(projectDto.getMotionDesign()))
                .build();
    }

    private ProjectDto map(ProjectEntity projectEntity) {
        return ProjectDto.builder()
                .customer(projectEntity.getCustomer())
                .owner(map(projectEntity.getOwner()))
                .dateOfReceipt(projectEntity.getDateOfReceipt().toString())
                .writer(map(projectEntity.getWriters()))
                .motionDesign(map(projectEntity.getMotionDesigners()))
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

    private UserDto map(UserEntity userEntity){
        return UserDto.builder()
                .loginName(userEntity.getLoginName())
                .role(userEntity.getRole())
                .build();
    }
    private UserEntity map(UserDto userDto) {
        return UserEntity.builder()
                .loginName(userDto.getLoginName())
                .role(userDto.getRole())
                .build();
    }

    private List<UserDto> map(Set<UserEntity> userEntities){
        List<UserDto> userDtoList = new LinkedList<>();
        for (UserEntity userEntity : userEntities) {
            userDtoList.add(map(userEntity));
        }
        return userDtoList;
    }
    private Set<UserEntity> mapUserDto(List<UserDto> userDtos){
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (UserDto userDto : userDtos) {
            userEntitySet.add(map(userDto));
        }
        return userEntitySet;
    }
}
