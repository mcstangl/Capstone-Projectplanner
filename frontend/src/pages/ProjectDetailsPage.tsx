import { FC, useCallback, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { useParams } from 'react-router-dom'
import { findProjectByTitle } from '../service/api-service'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { Button } from '../components/Button'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import ProjectDetailsEdit from '../components/ProjectDetailsEdit'
import ProjectDetails from '../components/ProjectDetails'
import Milestone from '../components/Milestone'
import styled from 'styled-components/macro'
import MainStyle from '../components/MainStyle'
import Loader from '../components/Loader'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import { LinkStyle } from '../components/LinkStyle'

interface RouteParams {
  projectTitle: string
}

const ProjectDetailsPage: FC = () => {
  const { projectTitle } = useParams<RouteParams>()
  const { token, authUser } = useContext(AuthContext)
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(true)

  const [project, setProject] = useState<ProjectDto>()

  const [editMode, setEditMode] = useState<boolean>()

  useEffect(() => {
    if (token) {
      findProjectByTitle(projectTitle, token)
        .then(setProject)
        .catch(error => setError(error.response.data))
        .finally(() => setLoading(false))
    }
  }, [projectTitle, token])

  const switchEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else {
      setEditMode(true)
    }
  }

  const fetchProject = () => {
    if (token && project) {
      return findProjectByTitle(project.title, token)
        .then(setProject)
        .catch(error => setError(error.response.data))
    }
  }

  const updateProjectState = (projectDto: ProjectDto) => {
    setProject(projectDto)
  }
  const updateErrorState = useCallback(
    (restExceptionDto: RestExceptionDto | undefined) => {
      setError(restExceptionDto)
    },
    [setError]
  )

  const onClickHandler = () => {
    switchEditMode()
  }

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        <ButtonGroupFlexbox>
          <LinkStyle to="/projects">Zurück zur Liste</LinkStyle>
          {!editMode && authUser && authUser.role === 'ADMIN' && (
            <Button onClick={onClickHandler}>Edit</Button>
          )}
        </ButtonGroupFlexbox>
        {loading && <Loader />}
        {!loading && (
          <ProjectDetailsStyle>
            <section>
              {!editMode && <ProjectDetails project={project} />}
              {editMode && (
                <ProjectDetailsEdit
                  updateErrorState={updateErrorState}
                  project={project}
                  switchEditMode={switchEditMode}
                  updateProjectState={updateProjectState}
                />
              )}

              {error && <p>{error.message}</p>}
            </section>

            {project && !editMode && (
              <Milestone fetchProject={fetchProject} project={project} />
            )}
          </ProjectDetailsStyle>
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default ProjectDetailsPage

const ProjectDetailsStyle = styled.section`
  width: 100%;
  display: flex;
  flex-direction: column;
`
