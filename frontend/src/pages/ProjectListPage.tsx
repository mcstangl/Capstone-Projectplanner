import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { ProjectDto } from '../dtos/ProjectDto'
import { findAllProjects } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link } from 'react-router-dom'
import styled from 'styled-components/macro'
import { LinkGroup } from '../components/LinkGroup'
import Loader from '../components/Loader'
import MainStyle from '../components/MainStyle'

const ProjectListPage: FC = () => {
  const { token, authUser } = useContext(AuthContext)
  const [loading, setLoading] = useState(true)
  const [projects, setProjects] = useState<ProjectDto[]>()
  const [search, setSearch] = useState('')

  let filteredProjects: ProjectDto[] = []
  if (projects) {
    filteredProjects = projects.filter(
      project =>
        project.title.toUpperCase().includes(search.toUpperCase()) ||
        project.customer.toUpperCase().includes(search.toUpperCase()) ||
        project.owner.loginName.toUpperCase().includes(search.toUpperCase()) ||
        project.writer.find(writer =>
          writer.loginName.toUpperCase().includes(search.toUpperCase())
        ) ||
        project.motionDesign.find(motionDesigner =>
          motionDesigner.loginName.toUpperCase().includes(search.toUpperCase())
        )
    )
  }

  useEffect(() => {
    if (token) {
      findAllProjects(token)
        .then(setProjects)
        .catch(console.error)
        .finally(() => setLoading(false))
    }
  }, [token])

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        {authUser?.role === 'ADMIN' && (
          <LinkGroup>
            <Link to="/new-project">Neues Projekt erstellen</Link>
          </LinkGroup>
        )}
        {loading && <Loader />}
        {!loading && (
          <SearchInput
            type="text"
            value={search}
            onChange={event => setSearch(event.target.value)}
            placeholder="Suche in Projekten"
          />
        )}
        {!loading && (
          <List>
            <ListHeader key="header">
              <span>Eingangsdatum</span>
              <span>Kunde</span>
              <span>Titel</span>
              <span>Milestone</span>
              <span>Projektleitung</span>
              <span>Redaktion</span>
              <span>Motion Design</span>
            </ListHeader>
            {filteredProjects &&
              filteredProjects.length &&
              filteredProjects.map(project => (
                <ListItem
                  id={project.title}
                  key={project.title}
                  to={'/projects/' + project.title}
                >
                  <span>{project.dateOfReceipt}</span>
                  <span>{project.customer}</span>
                  <span>{project.title}</span>
                  {project.milestones && project.milestones[0] ? (
                    <span>
                      {project.milestones[0].title +
                        ' ' +
                        project.milestones[0].dueDate}
                    </span>
                  ) : (
                    <div />
                  )}
                  <span>{project.owner.loginName}</span>
                  {project.writer.map(writer => (
                    <span key={writer.loginName}>{writer.loginName}</span>
                  ))}
                  {project.motionDesign.map(motionDesigner => (
                    <span key={motionDesigner.loginName}>
                      {motionDesigner.loginName}
                    </span>
                  ))}
                </ListItem>
              ))}
          </List>
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default ProjectListPage

const SearchInput = styled.input`
  width: 30%;
  margin-bottom: var(--size-m);
  margin-right: var(--size-s);
  margin-top: var(--size-m);
  justify-self: right;

  &:focus {
    border: 2px solid white;
    outline: 2px solid var(--accentcolor);
  }
`

const List = styled.ul`
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
`
const ListItem = styled(Link)`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
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
  grid-template-columns: repeat(7, 1fr);
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);
`
