package de.mcstangl.projectplanner.controller;


import de.mcstangl.projectplanner.api.MilestoneDto;
import de.mcstangl.projectplanner.api.ProjectDto;
import de.mcstangl.projectplanner.api.UpdateProjectDto;
import de.mcstangl.projectplanner.api.UserDto;
import de.mcstangl.projectplanner.enums.ProjectStatus;
import de.mcstangl.projectplanner.enums.UserRole;
import de.mcstangl.projectplanner.model.MilestoneEntity;
import de.mcstangl.projectplanner.model.ProjectEntity;
import de.mcstangl.projectplanner.model.UserEntity;

import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

 abstract class Mapper {

    public ProjectEntity mapProject(UpdateProjectDto updateProjectDto) {
        return ProjectEntity.builder()
                .owner(mapUser(updateProjectDto.getOwner()))
                .customer(updateProjectDto.getCustomer())
                .dateOfReceipt(Date.valueOf(updateProjectDto.getDateOfReceipt()))
                .writers(mapUserList(updateProjectDto.getWriter()))
                .motionDesigners(mapUserList(updateProjectDto.getMotionDesign()))
                .title(updateProjectDto.getTitle())
                .status(convertStringToProjectStatus(updateProjectDto.getStatus()))
                .build();
    }


     public ProjectEntity mapProject(ProjectDto projectDto) {
        return ProjectEntity.builder()
                .customer(projectDto.getCustomer())
                .title(projectDto.getTitle())
                .dateOfReceipt(Date.valueOf(projectDto.getDateOfReceipt()))
                .writers(mapUserList(projectDto.getWriter()))
                .motionDesigners(mapUserList(projectDto.getMotionDesign()))
                .status(convertStringToProjectStatus(projectDto.getStatus()))
                .build();
    }

     public ProjectDto mapProject(ProjectEntity projectEntity) {
        return ProjectDto.builder()
                .customer(projectEntity.getCustomer())
                .owner(mapUser(projectEntity.getOwner()))
                .dateOfReceipt(projectEntity.getDateOfReceipt().toString())
                .writer(mapUser(projectEntity.getWriters()))
                .motionDesign(mapUser(projectEntity.getMotionDesigners()))
                .title(projectEntity.getTitle())
                .milestones(mapMilestone(projectEntity.getMilestones()))
                .status(convertProjectStatusToString(projectEntity.getStatus()))
                .build();
    }


     public List<ProjectDto> mapProject(List<ProjectEntity> projectEntityList) {
        List<ProjectDto> projectDtoList = new LinkedList<>();
        for (ProjectEntity projectEntity : projectEntityList) {
            projectDtoList.add(mapProject(projectEntity));
        }
        return projectDtoList;
    }

     public UserDto mapUser(UserEntity userEntity){
        return UserDto.builder()
                .loginName(userEntity.getLoginName())
                .role(convertUserRoleToString(userEntity.getRole()))
                .build();
    }
     public UserEntity mapUser(UserDto userDto) {
        return UserEntity.builder()
                .loginName(userDto.getLoginName())
                .role(convertStringToUserRole(userDto.getRole()))
                .build();
    }

     public List<UserDto> mapUser(Set<UserEntity> userEntities){
        List<UserDto> userDtoList = new LinkedList<>();
        for (UserEntity userEntity : userEntities) {
            userDtoList.add(mapUser(userEntity));
        }
        return userDtoList;
    }

     public List<UserDto> mapUser(List<UserEntity> userEntityList){
         List<UserDto> userDtoList = new LinkedList<>();
         for (UserEntity userEntity : userEntityList) {
             userDtoList.add(mapUser(userEntity));
         }
         return userDtoList;
     }
     public Set<UserEntity> mapUserList(List<UserDto> userDtos){
        Set<UserEntity> userEntitySet = new HashSet<>();
        for (UserDto userDto : userDtos) {
            userEntitySet.add(mapUser(userDto));
        }
        return userEntitySet;
    }

     public List<MilestoneDto> mapMilestone(List<MilestoneEntity> milestoneEntityList) {
         List<MilestoneDto> milestoneDtoList = new LinkedList<>();
         for (MilestoneEntity mileStoneEntity : milestoneEntityList) {
             milestoneDtoList.add(mapMilestone(mileStoneEntity));
         }
         return milestoneDtoList;
     }

     public MilestoneEntity mapMilestone(MilestoneDto milestoneDto) {
         Date dueDate = convertStringToDate(milestoneDto.getDueDate());
         Date dateFinished = convertStringToDate(milestoneDto.getDateFinished());
         return MilestoneEntity.builder()
                 .id(milestoneDto.getId())
                 .title(milestoneDto.getTitle())
                 .dueDate(dueDate)
                 .dateFinished(dateFinished)
                 .build();
     }

     public MilestoneDto mapMilestone(MilestoneEntity milestoneEntity) {
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

     public String convertProjectStatusToString(ProjectStatus projectStatus){
        if(projectStatus == null){
            return null;
        }
        return projectStatus.toString();
     }

     public ProjectStatus convertStringToProjectStatus(String projectStatusString){
        if(projectStatusString == null){
            return null;
        }
        return ProjectStatus.valueOf(projectStatusString);
     }

     public String convertUserRoleToString(UserRole userRole){
         if(userRole == null){
             return null;
         }
         return userRole.toString();
     }

     public UserRole convertStringToUserRole(String userRoleString){
         if(userRoleString == null){
             return null;
         }
         return UserRole.valueOf(userRoleString);
     }
 }


