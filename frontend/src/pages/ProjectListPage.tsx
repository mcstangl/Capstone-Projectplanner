import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { ProjectDto } from '../dtos/ProjectDto'
import { findAllProjects } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link } from 'react-router-dom'
import styled from 'styled-components/macro'
import { LinkGroup } from '../components/LinkGroup'

const ProjectListPage: FC = () => {
  const { token, authUser } = useContext(AuthContext)
  const [projects, setProjects] = useState<ProjectDto[]>()

  useEffect(() => {
    if (token) {
      findAllProjects(token).then(setProjects).catch(console.error)
    }
  }, [token])

  return (
    <PageLayout>
      <Header />
      <main>
        {authUser?.role === 'ADMIN' && (
          <LinkGroup>
            <Link to="/new-project">Neues Projekt erstellen</Link>
          </LinkGroup>
        )}
        <List>
          <ListHeader>
            <h4>Eingangsdatum</h4>
            <h4>Kunde</h4>
            <h4>Titel</h4>
            <h4>Projektleitung</h4>
            <h4>Redaktion</h4>
            <h4>Motion Design</h4>
          </ListHeader>
          {projects &&
            projects.length &&
            projects.map(project => (
              <ListItem
                id={project.title}
                key={project.title}
                to={'/projects/' + project.title}
              >
                <span>{project.dateOfReceipt}</span>
                <span>{project.customer}</span>
                <span>{project.title}</span>
                <span>{project.owner.loginName}</span>
                {project.writer.map(writer => (
                  <span>{writer.loginName}</span>
                ))}
                {project.motionDesign.map(motionDesigner => (
                  <span>{motionDesigner.loginName}</span>
                ))}
              </ListItem>
            ))}
        </List>
      </main>
    </PageLayout>
  )
}
export default ProjectListPage

const List = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
`
const ListItem = styled(Link)`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  text-decoration: none;
  color: black;

  &:hover {
    background-color: var(--gradient4);
  }
`

const ListHeader = styled.li`
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);

  h4 {
    margin: 0;
    padding: 0;
  }
`
