import {
  ChangeEvent,
  FC,
  FormEvent,
  useContext,
  useEffect,
  useState,
} from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Link, useParams } from 'react-router-dom'
import { findProjectByTitle, updateProject } from '../service/api-service'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { LinkGroup } from '../components/LinkGroup'
import styled from 'styled-components/macro'
import { Button } from '../components/Button'

interface RouteParams {
  projectTitle: string
}

const ProjectDetailsPage: FC = () => {
  const { projectTitle } = useParams<RouteParams>()
  const { token } = useContext(AuthContext)

  const [project, setProject] = useState<ProjectDto>()
  const [editMode, setEditMode] = useState<boolean>()

  const [formData, setFormData] = useState<ProjectDto>({
    customer: '',
    title: '',
  })

  useEffect(() => {
    if (token) {
      findProjectByTitle(projectTitle, token)
        .then(setProject)
        .catch(console.error)
    }
  }, [projectTitle, token])

  useEffect(() => {
    if (project) {
      setFormData({ customer: project.customer, title: project.title })
    }
  }, [project])

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    if (project && token) {
      const updateProjectDto = {
        title: project.title,
        newTitle: formData.title,
        customer: formData.customer,
      }
      updateProject(updateProjectDto, token)
        .then(setProject)
        .catch(console.error)
        .finally(() => setEditMode(false))
    }
  }

  const onChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const onClickHandler = () => {
    if (editMode) {
      setEditMode(false)
    } else {
      setEditMode(true)
    }
  }

  return (
    <PageLayout>
      <Header />
      <main>
        <LinkGroup>
          <Link to="/projects">Zurück zur Liste</Link>
        </LinkGroup>
        <ProjectDetails onSubmit={submitHandler}>
          <h4>Kunde</h4>
          {editMode ? (
            <input
              name="customer"
              type="text"
              value={formData.customer}
              placeholder={project?.customer}
              onChange={onChangeHandler}
            />
          ) : (
            <span>{project?.customer}</span>
          )}
          <h4>Title</h4>
          {editMode ? (
            <input
              name="title"
              type="text"
              value={formData.title}
              placeholder={project?.title}
              onChange={onChangeHandler}
            />
          ) : (
            <span>{project?.title}</span>
          )}
          {editMode && (
            <Button type="button" onClick={onClickHandler}>
              Abbrechen
            </Button>
          )}
          {editMode && <Button>Speichern</Button>}
        </ProjectDetails>
        {editMode || <Button onClick={onClickHandler}>Edit</Button>}
      </main>
    </PageLayout>
  )
}
export default ProjectDetailsPage

const ProjectDetails = styled.form`
  max-width: 300px;
  display: grid;
  grid-template-columns: min-content 1fr;
  grid-gap: var(--size-s);

  h4 {
    margin: 0;
    padding: 0;
  }
`
