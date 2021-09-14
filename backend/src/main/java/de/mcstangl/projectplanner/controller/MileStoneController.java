package de.mcstangl.projectplanner.controller;

import de.mcstangl.projectplanner.api.MileStoneDto;
import de.mcstangl.projectplanner.model.MileStoneEntity;
import de.mcstangl.projectplanner.service.MileStoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("api/project-planner/milestone")
public class MileStoneController {

    private final MileStoneService mileStoneService;

    @Autowired
    public MileStoneController(MileStoneService mileStoneService) {
        this.mileStoneService = mileStoneService;
    }

    @GetMapping("{projectTitle}")
    public ResponseEntity<List<MileStoneDto>> findAllByProjectTitle(@PathVariable String projectTitle){
        List<MileStoneEntity> mileStoneEntityList = mileStoneService.findAllByProjectTitle(projectTitle);
        return ok(map(mileStoneEntityList));
    }

    private List<MileStoneDto> map(List<MileStoneEntity> mileStoneEntityList) {
        List<MileStoneDto> mileStoneDtoList = new LinkedList<>();
        for (MileStoneEntity mileStoneEntity : mileStoneEntityList) {
            mileStoneDtoList.add(map(mileStoneEntity));
        }
        return mileStoneDtoList;
    }

    private MileStoneDto map(MileStoneEntity mileStoneEntity) {
        return MileStoneDto.builder()
                .title(mileStoneEntity.getTitle())
                .dueDate(mileStoneEntity.getDueDate().toString())
                .dateFinished(mileStoneEntity.getDateFinished().toString())
                .build();
    }
}
