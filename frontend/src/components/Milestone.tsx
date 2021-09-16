import { FC, useState } from 'react'
import { Button } from './Button'
import MilestoneEdit from './MilestoneEdit'
import { ProjectDto } from '../dtos/ProjectDto'
import styled from 'styled-components/macro'
import MilestoneList from './MilestoneList'

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
      {project.milestones && project.milestones.length > 0 && (
        <MilestoneList
          fetchProject={fetchProject}
          milestones={project.milestones}
        />
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
