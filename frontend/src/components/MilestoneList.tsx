import { FC } from 'react'
import styled from 'styled-components/macro'
import { MilestoneDto } from '../dtos/MilestoneDto'
import MilestoneListItem from './MilestoneListItem'

interface MilestoneListProps {
  milestones: MilestoneDto[]
  fetchProject: () => Promise<void> | undefined
}

const MilestoneList: FC<MilestoneListProps> = ({
  milestones,
  fetchProject,
}) => {
  return (
    <MilestoneListStyle>
      <ListHeader>
        <span>Milestone</span>
        <span>Fällig am</span>
        <span>Fertig am</span>
      </ListHeader>
      {milestones.map(milestone => (
        <MilestoneListItem
          key={'' + milestone.id}
          fetchProject={fetchProject}
          milestone={milestone}
        />
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

  section:nth-child(2n) {
    background-color: var(--lightgrey);
  }
`

const ListHeader = styled.li`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);
`
