package de.mcstangl.projectplanner.service;

import de.mcstangl.projectplanner.model.MileStoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.repository.MileStoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MileStoneService {

    private final MileStoneRepository mileStoneRepository;
    private final ProjectService projectService;

    @Autowired
    public MileStoneService(MileStoneRepository mileStoneRepository, ProjectService projectService) {
        this.mileStoneRepository = mileStoneRepository;
        this.projectService = projectService;
    }

    public MileStoneEntity createNewMileStone(MileStoneEntity mileStone) {
        return mileStoneRepository.save(mileStone);
    }

    public List<MileStoneEntity> findAllByProjectTitle(String projectTitle){
        ProjectEntity projectEntity = projectService
                .findByTitle(projectTitle)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Das Projekt mit dem Titel %s konnte nicht gefunden werden", projectTitle)));

        return mileStoneRepository.findAllByProjectEntity(projectEntity);
    }
}
