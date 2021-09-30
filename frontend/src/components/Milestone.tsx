import { FC, useContext, useState } from 'react'
import { Button } from './Button'
import MilestoneEdit from './MilestoneEdit'
import { ProjectDto } from '../dtos/ProjectDto'
import styled from 'styled-components/macro'
import MilestoneList from './MilestoneList'
import AuthContext from '../auth/AuthContext'

interface MilestoneProps {
  project: ProjectDto
  fetchProject: () => Promise<void> | undefined
}

const Milestone: FC<MilestoneProps> = ({ project, fetchProject }) => {
  const [editMode, setEditMode] = useState(false)
  const { authUser } = useContext(AuthContext)

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <MilestoneStyle>
      {authUser && authUser.role === 'ADMIN' && (
        <div>
          <AddMilestoneButton onClick={switchEditMode}>
            Milestone hinzufügen
          </AddMilestoneButton>
        </div>
      )}
      {project.milestones && project.milestones.length > 0 && (
        <MilestoneList
          fetchProject={fetchProject}
          milestones={project.milestones}
        />
      )}

      {editMode && authUser && authUser.role === 'ADMIN' && (
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
    justify-self: right;
  }
`
const AddMilestoneButton = styled(Button)`
  margin: var(--size-l);
`
