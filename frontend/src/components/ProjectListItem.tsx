import { FC, useEffect, useState } from 'react'
import styled, { css } from 'styled-components/macro'
import { useHistory } from 'react-router-dom'
import { ProjectDto } from '../dtos/ProjectDto'
import { MilestoneDto } from '../dtos/MilestoneDto'

interface ProjectListItemProps {
  project: ProjectDto
  position: number
  theme: string
  archive?: boolean
}

const ProjectListItem: FC<ProjectListItemProps> = ({
  project,
  position,
  theme,
  archive,
}) => {
  const [nextMilestone, setNextMilestone] = useState<MilestoneDto>()
  const history = useHistory()

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

  const handleOnClick = () => {
    if (!archive) {
      history.push('/projects/' + project.title)
    } else console.log('archive')
  }

  return (
    <ListItem theme={theme} id={project.title} onClick={handleOnClick}>
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

const ListItem = styled.section`
  display: grid;
  grid-template-columns: var(--size-xxl) repeat(7, 1fr);
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  text-decoration: none;
  color: black;

  &:hover {
    ${props =>
      props.theme === 'archive'
        ? css`
            background-color: var(--accentcolor-gradient);
          `
        : css`
            background-color: var(--gradient4);
          `};
  }
`
