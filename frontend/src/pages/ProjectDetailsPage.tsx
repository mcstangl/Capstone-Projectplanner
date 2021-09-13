import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Link, useParams } from 'react-router-dom'
import { findProjectByTitle } from '../service/api-service'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { LinkGroup } from '../components/LinkGroup'
import styled from 'styled-components/macro'
import { Button } from '../components/Button'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import ProjectDetailsEdit from '../components/ProjectDetailsEdit'

interface RouteParams {
  projectTitle: string
}

const ProjectDetailsPage: FC = () => {
  const { projectTitle } = useParams<RouteParams>()
  const { token, authUser } = useContext(AuthContext)
  const [error, setError] = useState<RestExceptionDto>()

  const [project, setProject] = useState<ProjectDto>()

  const [editMode, setEditMode] = useState<boolean>()

  useEffect(() => {
    if (token) {
      findProjectByTitle(projectTitle, token)
        .then(setProject)
        .catch(error => setError(error.response.data))
    }
  }, [projectTitle, token])

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else {
      setEditMode(true)
    }
  }

  const updateProjectState = (projectDto: ProjectDto) => {
    setProject(projectDto)
  }
  const updateErrorState = (restExceptionDto: RestExceptionDto | undefined) => {
    setError(restExceptionDto)
  }

  const onClickHandler = () => {
    switchEditMode()
  }

  return (
    <PageLayout>
      <Header />
      <main>
        <LinkGroup>
          <Link to="/projects">Zurück zur Liste</Link>
        </LinkGroup>
        <ProjectDetails>
          <h4>Kunde</h4>

          <span>{project?.customer}</span>

          <h4>Title</h4>

          <span>{project?.title}</span>

          <h4>Projektleitung</h4>

          <span>{project?.owner.loginName}</span>

          <h4>Redaktion</h4>

          <span>{project?.writer[0] ? project?.writer[0].loginName : ''}</span>

          <h4>Motion Design</h4>

          <span>
            {project?.motionDesign[0] ? project?.motionDesign[0].loginName : ''}
          </span>
        </ProjectDetails>
        {editMode && (
          <ProjectDetailsEdit
            updateErrorState={updateErrorState}
            project={project}
            switchEditMode={switchEditMode}
            updateProjectState={updateProjectState}
          />
        )}
        {!editMode && authUser && authUser.role === 'ADMIN' && (
          <Button onClick={onClickHandler}>Edit</Button>
        )}
        {error && <p>{error.message}</p>}
      </main>
    </PageLayout>
  )
}
export default ProjectDetailsPage

const ProjectDetails = styled.section`
  max-width: 300px;
  display: grid;
  grid-template-columns: min-content 1fr;
  grid-gap: var(--size-s);

  h4 {
    margin: 0;
    padding: 0;
  }
`
