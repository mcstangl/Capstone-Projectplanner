package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.MileStoneDto;
import de.mcstangl.projectplanner.model.MileStoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.service.MileStoneService;
import de.mcstangl.projectplanner.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class MileStoneController {

    private final MileStoneService mileStoneService;
    private final ProjectService projectService;

    @Autowired
    public MileStoneController(MileStoneService mileStoneService, ProjectService projectService) {
        this.mileStoneService = mileStoneService;
        this.projectService = projectService;
    }

    @GetMapping("{projectTitle}")
    public ResponseEntity<List<MileStoneDto>> findAllByProjectTitle(@PathVariable String projectTitle){
        List<MileStoneEntity> mileStoneEntityList = mileStoneService.findAllByProjectTitle(projectTitle);
        return ok(map(mileStoneEntityList));
    }

    @PostMapping
    public ResponseEntity<MileStoneDto> createNewMilestone(@RequestBody MileStoneDto mileStoneDto){

        hasText(mileStoneDto.getTitle(), "Ein Milestone muss einen Titel haben");

        ProjectEntity projectEntity = projectService.findByTitle(mileStoneDto.getProjectTitle())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Projekt mit dem Titel %s konnte nicht gefunden werden", mileStoneDto.getProjectTitle())));

        MileStoneEntity mileStoneEntity = map(mileStoneDto);
        mileStoneEntity.setProjectEntity(projectEntity);

        MileStoneEntity newMileStone = mileStoneService.createNewMileStone(mileStoneEntity);

        return ok(map(newMileStone));
    }


    private List<MileStoneDto> map(List<MileStoneEntity> mileStoneEntityList) {
        List<MileStoneDto> mileStoneDtoList = new LinkedList<>();
        for (MileStoneEntity mileStoneEntity : mileStoneEntityList) {
            mileStoneDtoList.add(map(mileStoneEntity));
        }
        return mileStoneDtoList;
    }
    private MileStoneEntity map(MileStoneDto mileStoneDto) {
        return MileStoneEntity.builder()
                .title(mileStoneDto.getTitle())
                .dueDate(Date.valueOf(mileStoneDto.getDueDate()))
                .dateFinished(Date.valueOf(mileStoneDto.getDateFinished()))
                .build();
    }

    private MileStoneDto map(MileStoneEntity mileStoneEntity) {
        return MileStoneDto.builder()
                .title(mileStoneEntity.getTitle())
                .dueDate(mileStoneEntity.getDueDate().toString())
                .projectTitle(mileStoneEntity.getProjectEntity().getTitle())
                .dateFinished(mileStoneEntity.getDateFinished().toString())
                .build();
    }


}
