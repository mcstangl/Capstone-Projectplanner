import { FC, useState } from 'react'
import { Button } from './Button'
import MilestoneEdit from './MilestoneEdit'
import { ProjectDto } from '../dtos/ProjectDto'
import styled from 'styled-components/macro'

interface MilestoneProps {
  project: ProjectDto
  fetchProject: () => void
}

const Milestone: FC<MilestoneProps> = ({ project, fetchProject }) => {
  const [editMode, setEditMode] = useState(false)

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <MilestoneStyle>
      {project.milestones && (
        <MilestoneList>
          <ListHeader>
            <h4>Title</h4>
            <h4>Fällig am</h4>
            <h4>Fertig am</h4>
          </ListHeader>
          {project.milestones.map(milestone => (
            <MilestoneListItem key={'' + milestone.id}>
              <span>{milestone.title}</span>
              <span>{milestone.dueDate}</span>
              <span>{milestone.dateFinished}</span>
            </MilestoneListItem>
          ))}
        </MilestoneList>
      )}
      {!editMode && (
        <div>
          <Button onClick={switchEditMode}>Milestone hinzufügen</Button>
        </div>
      )}
      {editMode && (
        <MilestoneEdit
          fetchProject={fetchProject}
          switchEditMode={switchEditMode}
          projectTitle={project.title}
        />
      )}
    </MilestoneStyle>
  )
}
export default Milestone

const MilestoneStyle = styled.section`
  display: grid;
  justify-content: space-between;
  grid-template-columns: 1fr;
  grid-template-rows: min-content 1fr;

  div {
    justify-self: center;
  }
`

const MilestoneList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
`
const MilestoneListItem = styled.li`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  text-decoration: none;
  color: black;

  &:hover {
    background-color: var(--gradient4);
  }
`

const ListHeader = styled.li`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);

  h4 {
    margin: 0;
    padding: 0;
  }
`
