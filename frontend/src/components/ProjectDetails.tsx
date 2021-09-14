import { FC } from 'react'
import styled from 'styled-components/macro'
import { ProjectDto } from '../dtos/ProjectDto'

interface ProjectDetailsProps {
  project?: ProjectDto
}

const ProjectDetails: FC<ProjectDetailsProps> = ({ project }) => {
  return (
    <ProjectDetailsStyle>
      <h4>Kunde</h4>

      <span>{project?.customer}</span>

      <h4>Title</h4>

      <span>{project?.title}</span>

      <h4>Projektleitung</h4>

      <span>{project?.owner.loginName}</span>

      <h4>Redaktion</h4>

      <span>{project?.writer[0] ? project?.writer[0].loginName : ''}</span>

      <h4>Motion Design</h4>

      <span>
        {project?.motionDesign[0] ? project?.motionDesign[0].loginName : ''}
      </span>
    </ProjectDetailsStyle>
  )
}
export default ProjectDetails

const ProjectDetailsStyle = styled.section`
  max-width: 300px;
  display: grid;
  grid-template-columns: min-content 1fr;
  grid-gap: var(--size-s);

  h4 {
    margin: 0;
    padding: 0;
  }
`
