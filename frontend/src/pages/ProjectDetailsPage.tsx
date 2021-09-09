import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Link, useParams } from 'react-router-dom'
import { findProjectByTitle } from '../service/api-service'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { LinkGroup } from '../components/LinkGroup'
import styled from 'styled-components/macro'

interface RouteParams {
  projectTitle: string
}

const ProjectDetailsPage: FC = () => {
  const { projectTitle } = useParams<RouteParams>()
  const { token } = useContext(AuthContext)

  const [project, setProject] = useState<ProjectDto>()

  useEffect(() => {
    if (token) {
      findProjectByTitle(projectTitle, token)
        .then(setProject)
        .catch(console.error)
    }
  }, [projectTitle, token])

  return (
    <PageLayout>
      <Header />
      <main>
        <LinkGroup>
          <Link to="/projects">Zurück zur Liste</Link>
        </LinkGroup>
        <ProjectDetails>
          <h4>Kunde</h4> <span>{project?.customer}</span>
          <h4>Title</h4> <span>{project?.title}</span>
        </ProjectDetails>
        <LinkGroup>
          <Link to="/projects">Editieren</Link>
        </LinkGroup>
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
