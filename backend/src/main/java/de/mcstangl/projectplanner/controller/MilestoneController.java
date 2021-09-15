package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.MilestoneDto;
import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;
import de.mcstangl.projectplanner.service.MilestoneService;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.util.Assert.hasText;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/milestone")
public class MilestoneController {

    private final MilestoneService mileStoneService;
    private final ProjectService projectService;

    @Autowired
    public MilestoneController(MilestoneService mileStoneService, ProjectService projectService) {
        this.mileStoneService = mileStoneService;
        this.projectService = projectService;
    }

    @GetMapping("{projectTitle}")
    public ResponseEntity<List<MilestoneDto>> findAllByProjectTitle(@PathVariable String projectTitle) {
        List<MilestoneEntity> milestoneEntityList = mileStoneService.findAllByProjectTitle(projectTitle);
        return ok(map(milestoneEntityList));
    }

    @PostMapping
    public ResponseEntity<MilestoneDto> createNewMilestone(@AuthenticationPrincipal UserEntity authUser, @RequestBody MilestoneDto milestoneDto) {

        if (isAdmin(authUser)) {
            hasText(milestoneDto.getTitle(), "Ein Milestone muss einen Titel haben");

            ProjectEntity projectEntity = projectService.findByTitle(milestoneDto.getProjectTitle())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", milestoneDto.getProjectTitle())));

            MilestoneEntity mileStoneEntity = map(milestoneDto);
            mileStoneEntity.setProjectEntity(projectEntity);

            MilestoneEntity newMileStone = mileStoneService.createNewMileStone(mileStoneEntity);

            return ok(map(newMileStone));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

    private List<MilestoneDto> map(List<MilestoneEntity> milestoneEntityList) {
        List<MilestoneDto> milestoneDtoList = new LinkedList<>();
        for (MilestoneEntity mileStoneEntity : milestoneEntityList) {
            milestoneDtoList.add(map(mileStoneEntity));
        }
        return milestoneDtoList;
    }

    private MilestoneEntity map(MilestoneDto milestoneDto) {
        Date dueDate = convertStringToDate(milestoneDto.getDueDate());
        Date dateFinished = convertStringToDate(milestoneDto.getDateFinished());
        return MilestoneEntity.builder()
                .id(milestoneDto.getId())
                .title(milestoneDto.getTitle())
                .dueDate(dueDate)
                .dateFinished(dateFinished)
                .build();
    }

    private MilestoneDto map(MilestoneEntity milestoneEntity) {
        String dueDate = convertDateToString(milestoneEntity.getDueDate());
        String dateFinished = convertDateToString(milestoneEntity.getDateFinished());
        return MilestoneDto.builder()
                .id(milestoneEntity.getId())
                .title(milestoneEntity.getTitle())
                .dueDate(dueDate)
                .projectTitle(milestoneEntity.getProjectEntity().getTitle())
                .dateFinished(dateFinished)
                .build();
    }

    private Date convertStringToDate(String dateString) {
        try {
            return Date.valueOf(dateString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    private String convertDateToString(Date date){
        if(date == null){
            return null;
        }return date.toString();
    }
}
