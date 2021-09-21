import { FC } from 'react'
import styled from 'styled-components/macro'
import { ProjectDto } from '../dtos/ProjectDto'
import ProjectListItem from './ProjectListItem'

interface ProjectListProps {
  projects: ProjectDto[]
  theme: string
  archive?: boolean
}

const ProjectList: FC<ProjectListProps> = ({ projects, theme, archive }) => {
  return (
    <List>
      <ListHeader key="header">
        <span>Pos.</span>
        <span>Eingangsdatum</span>
        <span>Kunde</span>
        <span>Titel</span>
        <span>Milestone</span>
        <span>Projektleitung</span>
        <span>Redaktion</span>
        <span>Motion Design</span>
      </ListHeader>
      {projects &&
        projects.length &&
        projects.map((project, index) => (
          <ProjectListItem
            theme={theme}
            position={index + 1}
            key={project.title}
            project={project}
            archive={archive}
          />
        ))}
    </List>
  )
}
export default ProjectList

const List = styled.section`
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
`

const ListHeader = styled.div`
  display: grid;
  grid-template-columns: var(--size-xxl) repeat(7, 1fr);
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);
`
