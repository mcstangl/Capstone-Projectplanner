import { FC, useEffect, useState } from 'react'
import styled from 'styled-components/macro'
import { Link } from 'react-router-dom'
import { ProjectDto } from '../dtos/ProjectDto'
import { MilestoneDto } from '../dtos/MilestoneDto'

interface ProjectListItemProps {
  project: ProjectDto
  position: number
}

const ProjectListItem: FC<ProjectListItemProps> = ({ project, position }) => {
  const [nextMilestone, setNextMilestone] = useState<MilestoneDto>()

  useEffect(() => {
    let filteredMilestones: MilestoneDto[] = []
    if (project.milestones) {
      filteredMilestones = project.milestones.filter(
        milestone => milestone.dateFinished === null
      )
    }
    if (filteredMilestones) {
      setNextMilestone(filteredMilestones[0])
    }
  }, [project.milestones])

  return (
    <ListItem id={project.title} to={'/projects/' + project.title}>
      <span>{position}</span>
      <span>{project.dateOfReceipt}</span>
      <span>{project.customer}</span>
      <span>{project.title}</span>
      {nextMilestone ? (
        <span>{nextMilestone.dueDate + ' ' + nextMilestone.title}</span>
      ) : (
        <div />
      )}
      <span>{project.owner.loginName}</span>
      {project.writer.map(writer => (
        <span key={writer.loginName}>{writer.loginName}</span>
      ))}
      {project.motionDesign.map(motionDesigner => (
        <span key={motionDesigner.loginName}>{motionDesigner.loginName}</span>
      ))}
    </ListItem>
  )
}

export default ProjectListItem

const ListItem = styled(Link)`
  display: grid;
  grid-template-columns: var(--size-xxl) repeat(7, 1fr);
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  text-decoration: none;
  color: black;

  &:hover {
    background-color: var(--gradient4);
  }
`
