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
import ProjectList from '../components/ProjectList'

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
        {!loading && <ProjectList projects={filteredProjects} />}
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
