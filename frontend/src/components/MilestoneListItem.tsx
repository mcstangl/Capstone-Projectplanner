import { FC, useState } from 'react'
import styled from 'styled-components/macro'
import { MilestoneDto } from '../dtos/MilestoneDto'
import MilestoneEdit from './MilestoneEdit'

interface MilestoneListItemProps {
  milestone: MilestoneDto
  fetchProject: () => void
}

const MilestoneListItem: FC<MilestoneListItemProps> = ({
  milestone,
  fetchProject,
}) => {
  const [editMode, setEditMode] = useState(false)

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <section>
      {!editMode && (
        <MilestoneListItemStyle onClick={switchEditMode}>
          <span>{milestone.title}</span>
          <span>{milestone.dueDate}</span>
          <span>{milestone.dateFinished}</span>
        </MilestoneListItemStyle>
      )}
      {editMode && (
        <MilestoneEdit
          fetchProject={fetchProject}
          switchEditMode={switchEditMode}
          projectTitle={milestone.projectTitle}
          milestone={milestone}
        />
      )}
    </section>
  )
}
export default MilestoneListItem

const MilestoneListItemStyle = styled.li`
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
