import { FC, useContext, useEffect, useState } from 'react'
import styled, { css } from 'styled-components/macro'
import { useHistory } from 'react-router-dom'
import { ProjectDto } from '../dtos/ProjectDto'
import { MilestoneDto } from '../dtos/MilestoneDto'
import { Button } from './Button'
import { restoreFromArchive } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import ErrorPopup from './ErrorPopup'

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
  const [restoreMode, setRestoreMode] = useState(false)
  const [error, setError] = useState()
  const history = useHistory()

  useEffect(() => {
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
        .then(() => updateProjects())
        .then(() => setRestoreMode(false))
        .catch(error => {
          setRestoreMode(false)
          setError(error.data.message)
        })
    }
  }

  return (
    <section>
      <ListItem theme={theme} id={project.title} onClick={handleOnClick}>
        <span>{position}</span>
        <span>{project.dateOfReceipt}</span>
        <span>{project.customer}</span>
        <span>{project.title}</span>
        {nextMilestone ? (
          <span>{nextMilestone.dueDate + ' ' + nextMilestone.title}</span>
        ) : (
          <div />
        )}
        <span>{project.owner.loginName}</span>
        {project.writer.map(writer => (
          <span key={writer.loginName}>{writer.loginName}</span>
        ))}
        {project.motionDesign.map(motionDesigner => (
          <span key={motionDesigner.loginName}>{motionDesigner.loginName}</span>
        ))}
      </ListItem>
      {restoreMode && authUser && authUser.role === 'ADMIN' && (
        <DeletePopup>
          <h3>Projekt</h3>
          <p>{project.title}</p>
          <Button theme="secondary" onClick={handleRestoreOnClick}>
            Wiederherstellen
          </Button>
          <Button theme="secondary" onClick={() => setRestoreMode(false)}>
            Abbrechen
          </Button>
        </DeletePopup>
      )}
      {error && <ErrorPopup message={error} />}
    </section>
  )
}

export default ProjectListItem

const DeletePopup = styled.section`
  position: absolute;
  background-color: white;
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

const ListItem = styled.section`
  display: grid;
  grid-template-columns: var(--size-xxl) repeat(7, 1fr);
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
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
