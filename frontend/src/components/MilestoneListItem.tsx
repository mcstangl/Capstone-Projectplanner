import { FC } from 'react'
import styled from 'styled-components/macro'
import { MilestoneDto } from '../dtos/MilestoneDto'

interface MilestoneListItemProps {
  milestone: MilestoneDto
}

const MilestoneListItem: FC<MilestoneListItemProps> = ({ milestone }) => {
  return (
    <MilestoneListItemStyle>
      <span>{milestone.title}</span>
      <span>{milestone.dueDate}</span>
      <span>{milestone.dateFinished}</span>
    </MilestoneListItemStyle>
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
