import { FC, useContext, useEffect, useState } from 'react'
import styled, { css } from 'styled-components/macro'
import { useHistory } from 'react-router-dom'
import { ProjectDto } from '../dtos/ProjectDto'
import { MilestoneDto } from '../dtos/MilestoneDto'
import { Button } from './Button'
import { restoreFromArchive } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import ErrorPopup from './ErrorPopup'
import {
  Column2Style,
  Column5Style,
  Column6Style,
  Column7Style,
  Column8Style,
  ListItemStyle,
} from './ProjectListGridStyle'

interface ProjectListItemProps {
  project: ProjectDto
  position: number
  theme: string
  archive?: boolean
  updateProjects?: () => Promise<void> | undefined
}

const ProjectListItem: FC<ProjectListItemProps> = ({
  project,
  position,
  theme,
  archive,
  updateProjects,
}) => {
  const { token, authUser } = useContext(AuthContext)
  const [nextMilestone, setNextMilestone] = useState<MilestoneDto>()
  const [milestonesIndicators, setMilestoneIndicators] = useState<
    JSX.Element[]
  >([])
  const [restoreMode, setRestoreMode] = useState(false)
  const [error, setError] = useState()
  const history = useHistory()

  useEffect(() => {
    setNextMilestone(undefined)
    let filteredMilestones: MilestoneDto[] = []
    if (project.milestones) {
      filteredMilestones = project.milestones.filter(
        milestone => milestone.dateFinished === null
      )
    }
    if (filteredMilestones) {
      setNextMilestone(filteredMilestones[0])
    }
  }, [project.milestones])

  const handleOnClick = () => {
    if (!archive) {
      history.push('/projects/' + project.title)
    } else setRestoreMode(true)
  }

  const handleRestoreOnClick = () => {
    if (token && updateProjects) {
      restoreFromArchive(token, project.title)
        .then(() => setRestoreMode(false))
        .then(() => updateProjects())
        .catch(error => {
          setRestoreMode(false)
          setError(error.data.message)
        })
    }
  }
  const resetErrorState = () => setError(undefined)

  useEffect(() => {
    setMilestoneIndicators([])
    if (project.milestones) {
      const milestoneProgressArray: JSX.Element[] = []
      for (const milestone of project.milestones) {
        if (milestone.dateFinished && milestone.id) {
          milestoneProgressArray.push(
            <MilestoneIndicatorLight
              id={milestone.id + ''}
              key={'indicator' + milestone.id}
              theme="done"
            />
          )
        } else if (!milestone.dateFinished && milestone.id) {
          milestoneProgressArray.push(
            <MilestoneIndicatorLight
              id={milestone.id + ''}
              key={'indicator' + milestone.id}
              theme="open"
            />
          )
        }
      }
      setMilestoneIndicators(milestoneProgressArray)
    }
  }, [project.milestones])

  return (
    <section>
      <ListItem theme={theme} id={project.title} onClick={handleOnClick}>
        <span>{position}</span>
        <MilestoneProgressStyle>{milestonesIndicators}</MilestoneProgressStyle>
        <Column2Style>{project.dateOfReceipt}</Column2Style>
        <span>{project.customer}</span>
        <span>{project.title}</span>
        {nextMilestone ? (
          <Column5Style>
            {nextMilestone.dueDate + ' ' + nextMilestone.title}
          </Column5Style>
        ) : (
          <div />
        )}
        <Column6Style>{project.owner.loginName}</Column6Style>
        {project.writer.map(writer => (
          <Column7Style key={writer.loginName}>{writer.loginName}</Column7Style>
        ))}
        {project.motionDesign.map(motionDesigner => (
          <Column8Style key={motionDesigner.loginName}>
            {motionDesigner.loginName}
          </Column8Style>
        ))}
      </ListItem>
      {restoreMode && authUser && authUser.role === 'ADMIN' && (
        <RestorePopup>
          <h3>Projekt</h3>
          <p>{project.title}</p>
          <Button theme="secondary" onClick={handleRestoreOnClick}>
            Wiederherstellen
          </Button>
          <Button theme="secondary" onClick={() => setRestoreMode(false)}>
            Abbrechen
          </Button>
        </RestorePopup>
      )}
      {error && (
        <ErrorPopup resetErrorState={resetErrorState} message={error} />
      )}
    </section>
  )
}

export default ProjectListItem

const RestorePopup = styled.section`
  position: fixed;
  background-color: white;
  top: 30%;
  right: 0;
  left: 0;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
  width: 250px;
  display: grid;
  grid-template-columns: 100%;
  justify-items: center;
  grid-gap: var(--size-l);
  border: 1px solid var(--secondarycolor);
  box-shadow: 3px 8px 12px grey;
  padding: var(--size-l);

  button {
    width: 100%;
  }
`

const ListItem = styled(ListItemStyle)`
  text-decoration: none;
  color: black;

  &:hover {
    ${props =>
      props.theme === 'archive'
        ? css`
            background-color: var(--accentcolor-gradient);
          `
        : css`
            background-color: var(--gradient4);
          `};
  }
`
const MilestoneProgressStyle = styled.span`
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: flex-start;
`
const MilestoneIndicatorLight = styled.div`
  margin: 2px;
  width: 8px;
  height: 8px;
  border-radius: 50%;

  ${props =>
    props.theme === 'done'
      ? css`
          background-color: var(--maincolor);
        `
      : css`
          background-color: var(--secondarycolor);
        `}
`
