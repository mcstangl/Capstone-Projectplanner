import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { ProjectDto } from '../dtos/ProjectDto'
import { findAllProjects } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link } from 'react-router-dom'
import styled from 'styled-components/macro'
import Loader from '../components/Loader'
import MainStyle from '../components/MainStyle'
import ProjectList from '../components/ProjectList'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import Switch from 'react-switch'
import { InputField } from '../components/Inputfield'

const ProjectListPage: FC = () => {
  const { token, authUser } = useContext(AuthContext)
  const [loading, setLoading] = useState(true)
  const [projects, setProjects] = useState<ProjectDto[]>()
  const [search, setSearch] = useState('')
  const [archiveOn, setArchiveOn] = useState(false)

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

  const handleArchiveSwitch = () => {
    if (archiveOn) {
      setArchiveOn(false)
    } else setArchiveOn(true)
  }

  const updateProjects: () => Promise<void> | undefined = () => {
    setLoading(true)
    if (token) {
      return findAllProjects(token)
        .then(setProjects)
        .finally(() => setLoading(false))
    }
  }

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        <ButtonGroupFlexbox>
          {!loading && !archiveOn && authUser?.role === 'ADMIN' ? (
            <LinkStyle to="/new-project">Neues Projekt erstellen</LinkStyle>
          ) : (
            <div />
          )}
          {!loading && (
            <ArchiveSwitchLabel>
              <Switch
                checked={archiveOn}
                onColor="#f39200"
                offColor="#006c5b"
                onChange={handleArchiveSwitch}
              />
              <span>Projektarchiv</span>
            </ArchiveSwitchLabel>
          )}
        </ButtonGroupFlexbox>

        {loading && <Loader />}
        {!loading && (
          <SearchBox>
            <SearchInput
              type="text"
              value={search}
              onChange={event => setSearch(event.target.value)}
              placeholder="Suche in Projekten"
            />
          </SearchBox>
        )}
        {!loading && !archiveOn && (
          <ProjectList
            theme=""
            projects={filteredProjects.filter(
              project => project.status === 'OPEN'
            )}
          />
        )}
        {!loading && archiveOn && (
          <ProjectList
            archive
            theme="archive"
            updateProjects={updateProjects}
            projects={filteredProjects.filter(
              project => project.status === 'ARCHIVE'
            )}
          />
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default ProjectListPage

const ArchiveSwitchLabel = styled.label`
  display: grid;
  grid-template-columns: min-content min-content;
  grid-gap: var(--size-m);
  align-items: center;
  font-size: 112%;
  margin-bottom: 0.5rem;
`

const SearchInput = styled(InputField)`
  width: 40%;
  margin-bottom: var(--size-m);
  margin-right: var(--size-l);
  margin-top: var(--size-m);
`
const SearchBox = styled.section`
  display: flex;
  justify-content: right;
  margin-bottom: 1rem;
`

const LinkStyle = styled(Link)`
  text-decoration: none;
  border: solid var(--maincolor) 1px;
  color: var(--maincolor);
  padding: 0.5rem 2rem;
  margin-bottom: 0.5rem;
  border-radius: 4px;
  transition-duration: 300ms;

  &:hover {
    background-color: var(--maincolor);
    color: white;
    transition-duration: 300ms;
  }
`
