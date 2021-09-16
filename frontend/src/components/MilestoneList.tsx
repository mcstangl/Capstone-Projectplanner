import { FC } from 'react'
import styled from 'styled-components/macro'
import { MilestoneDto } from '../dtos/MilestoneDto'
import MilestoneListItem from './MilestoneListItem'

interface MilestoneListProps {
  milestones: MilestoneDto[]
}

const MilestoneList: FC<MilestoneListProps> = ({ milestones }) => {
  return (
    <MilestoneListStyle>
      <ListHeader>
        <h4>Title</h4>
        <h4>Fällig am</h4>
        <h4>Fertig am</h4>
      </ListHeader>
      {milestones.map(milestone => (
        <MilestoneListItem key={'' + milestone.id} milestone={milestone} />
      ))}
    </MilestoneListStyle>
  )
}
export default MilestoneList

const MilestoneListStyle = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
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
