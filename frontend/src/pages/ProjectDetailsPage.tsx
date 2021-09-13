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
import {
  findAllUser,
  findProjectByTitle,
  updateProject,
} from '../service/api-service'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { LinkGroup } from '../components/LinkGroup'
import styled from 'styled-components/macro'
import { Button } from '../components/Button'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import { UserDto } from '../dtos/UserDto'

interface RouteParams {
  projectTitle: string
}

interface UpdateProjektFormData {
  customer: string
  title: string
  owner?: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}

const ProjectDetailsPage: FC = () => {
  const { projectTitle } = useParams<RouteParams>()
  const { token, authUser } = useContext(AuthContext)

  const [project, setProject] = useState<ProjectDto>()
  const [userList, setUserList] = useState<UserDto[]>()
  const [editMode, setEditMode] = useState<boolean>()
  const [error, setError] = useState<RestExceptionDto>()

  const [formData, setFormData] = useState<UpdateProjektFormData>({
    customer: '',
    title: '',
    writer: [],
    motionDesign: [],
  })

  useEffect(() => {
    if (token) {
      findProjectByTitle(projectTitle, token)
        .then(setProject)
        .catch(error => setError(error.response.data))
    }
  }, [projectTitle, token])

  useEffect(() => {
    if (project) {
      setFormData({
        customer: project.customer,
        title: project.title,
        motionDesign: project.motionDesign,
        writer: project.writer,
        owner: project.owner,
      })
    }
  }, [project])

  useEffect(() => {
    if (editMode && token) {
      findAllUser(token)
        .then(setUserList)
        .catch(error => setError(error.response.data))
    }
  }, [editMode, token])

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    setError(undefined)
    if (
      project &&
      token &&
      formData.title.trim() &&
      formData.customer.trim() &&
      formData.owner
    ) {
      const updateProjectDto = {
        title: project.title,
        newTitle: formData.title.trim(),
        customer: formData.customer.trim(),
        owner: formData.owner,
        writer: formData.writer,
        motionDesign: formData.motionDesign,
      }
      updateProject(updateProjectDto, token)
        .then(setProject)
        .then(() => setEditMode(false))
        .catch(error => setError(error.response.data))
    }
  }
  const handleSelectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const userToAdd = userList?.find(
      user => user.loginName === event.target.value
    )

    if (userToAdd) {
      setFormData({ ...formData, owner: userToAdd })
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
          <h4>Projektleitung</h4>
          {editMode && userList ? (
            <select
              onChange={handleSelectChange}
              defaultValue={project?.owner.loginName}
            >
              {userList?.map(user => (
                <option key={user.loginName} value={user.loginName}>
                  {user.loginName}
                </option>
              ))}
            </select>
          ) : (
            <span>{project?.owner.loginName}</span>
          )}

          {editMode && (
            <Button type="button" onClick={onClickHandler}>
              Abbrechen
            </Button>
          )}
          {editMode &&
            (formData.title.trim() && formData.customer.trim() ? (
              <Button>Speichern</Button>
            ) : (
              <Button disabled>Speichern</Button>
            ))}
        </ProjectDetails>
        {!editMode && authUser && authUser.role === 'ADMIN' && (
          <Button onClick={onClickHandler}>Edit</Button>
        )}
        {error && <p>{error.message}</p>}
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
