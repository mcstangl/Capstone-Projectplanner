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
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.util.Assert.hasText;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/milestone")
public class MilestoneController extends Mapper{

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
        return ok(mapMilestone(milestoneEntityList));
    }

    @PostMapping
    public ResponseEntity<MilestoneDto> createNewMilestone(@AuthenticationPrincipal UserEntity authUser, @RequestBody MilestoneDto milestoneDto) {

        if (isAdmin(authUser)) {
            MilestoneEntity milestoneEntity = getMilestoneEntity(milestoneDto);

            MilestoneEntity newMilestone = mileStoneService.createNewMilestone(milestoneEntity);

            return ok(mapMilestone(newMilestone));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    @PutMapping
    public ResponseEntity<MilestoneDto> updateMilestone(@AuthenticationPrincipal UserEntity authUser, @RequestBody MilestoneDto milestoneDto) {

        if(milestoneDto.getId() == null) {
            throw new IllegalArgumentException("Milestone ohne ID kann nicht upgedated werden");
        }

        if (isAdmin(authUser)) {
            MilestoneEntity milestoneEntity = getMilestoneEntity(milestoneDto);

            MilestoneEntity newMilestone = mileStoneService.updateMilestone(milestoneEntity);

            return ok(mapMilestone(newMilestone));
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private MilestoneEntity getMilestoneEntity(MilestoneDto milestoneDto) {
        hasText(milestoneDto.getTitle(), "Ein Milestone muss einen Titel haben");

        ProjectEntity projectEntity = projectService.findByTitle(milestoneDto.getProjectTitle())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", milestoneDto.getProjectTitle())));

        MilestoneEntity mileStoneEntity = mapMilestone(milestoneDto);
        mileStoneEntity.setProjectEntity(projectEntity);
        return mileStoneEntity;
    }

    private boolean isAdmin(UserEntity authUser) {
        return authUser.getRole().equals("ADMIN");
    }

}