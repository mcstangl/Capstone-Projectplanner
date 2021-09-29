import { FC } from 'react'
import styled from 'styled-components/macro'
import { ProjectDto } from '../dtos/ProjectDto'
import ProjectListItem from './ProjectListItem'
import {
  Column5Style,
  Column6Style,
  Column8Style,
  Column7Style,
  Column2Style,
  ListItemStyle,
} from './ProjectListGridStyle'

interface ProjectListProps {
  projects: ProjectDto[]
  theme: string
  archive?: boolean
  updateProjects?: () => Promise<void> | undefined
}

const ProjectList: FC<ProjectListProps> = ({
  projects,
  theme,
  archive,
  updateProjects,
}) => {
  return (
    <List>
      <ListHeader key="header">
        <span>Pos.</span>
        <span />
        <Column2Style>Eingangsdatum</Column2Style>
        <span>Kunde</span>
        <span>Titel</span>
        <Column5Style>Milestone</Column5Style>
        <Column6Style>Projektleitung</Column6Style>
        <Column7Style>Redaktion</Column7Style>
        <Column8Style>Motion Design</Column8Style>
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
            updateProjects={updateProjects}
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

  section:nth-child(2n) {
    background-color: var(--lightgrey);
  }
`

const ListHeader = styled(ListItemStyle)`
  border-bottom: solid 1px var(--secondarycolor);
`
