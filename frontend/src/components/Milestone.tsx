import { FC, useState } from 'react'
import { Button } from './Button'
import MilestoneEdit from './MilestoneEdit'
import { ProjectDto } from '../dtos/ProjectDto'

interface MilestoneProps {
  project: ProjectDto
}

const Milestone: FC<MilestoneProps> = ({ project }) => {
  const [editMode, setEditMode] = useState(false)

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <section>
      {!editMode && (
        <Button onClick={switchEditMode}>Milestone hinzufügen</Button>
      )}
      {editMode && (
        <MilestoneEdit
          switchEditMode={switchEditMode}
          projectTitle={project.title}
        />
      )}
    </section>
  )
}
export default Milestone
