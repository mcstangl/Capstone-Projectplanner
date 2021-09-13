import {
  ChangeEvent,
  FC,
  FormEvent,
  useContext,
  useEffect,
  useState,
} from 'react'
import UserSelect from './UserSelect'
import { Button } from './Button'
import styled from 'styled-components/macro'
import { findAllUser, updateProject } from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { UserDto } from '../dtos/UserDto'

interface ProjectDetailsEditProps {
  project?: ProjectDto
  switchEditMode: () => void
  updateProjectState: (project: ProjectDto) => void
  updateErrorState: (restExceptionDto: RestExceptionDto | undefined) => void
}

interface UpdateProjektFormData {
  customer: string
  title: string
  owner?: UserDto
  writer: UserDto[]
  motionDesign: UserDto[]
}

const ProjectDetailsEdit: FC<ProjectDetailsEditProps> = ({
  project,
  switchEditMode,
  updateProjectState,
  updateErrorState,
}) => {
  const { token } = useContext(AuthContext)
  const [userList, setUserList] = useState<UserDto[]>()

  const [formData, setFormData] = useState<UpdateProjektFormData>({
    customer: '',
    title: '',
    writer: [],
    motionDesign: [],
  })

  useEffect(() => {
    if (token) {
      findAllUser(token)
        .then(setUserList)
        .catch(error => updateErrorState(error.response.data))
    }
  }, [token, updateErrorState])

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

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    updateErrorState(undefined)
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
        .then(projectDto => updateProjectState(projectDto))
        .then(() => switchEditMode())
        .catch(error => updateErrorState(error.response.data))
    }
  }

  const onClickHandler = () => {
    switchEditMode()
  }

  const onChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const handleSelectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const fieldToChange = event.target.name
    const userToAdd = userList?.find(
      user => user.loginName === event.target.value
    )

    if (
      userToAdd &&
      (fieldToChange === 'writer' || fieldToChange === 'motionDesign')
    ) {
      setFormData({ ...formData, [fieldToChange]: [] })
      const userArray = []
      userArray.push(userToAdd)
      setFormData({ ...formData, [fieldToChange]: userArray })
    } else if (userToAdd) {
      setFormData({ ...formData, [fieldToChange]: userToAdd })
    } else setFormData({ ...formData, [fieldToChange]: [] })
  }

  return (
    <ProjectDetails onSubmit={submitHandler}>
      <h4>Kunde</h4>

      <input
        name="customer"
        type="text"
        value={formData.customer}
        placeholder={project?.customer}
        onChange={onChangeHandler}
      />
      <h4>Title</h4>

      <input
        name="title"
        type="text"
        value={formData.title}
        placeholder={project?.title}
        onChange={onChangeHandler}
      />
      <h4>Projektleitung</h4>
      {userList && (
        <UserSelect
          handleSelectChange={handleSelectChange}
          userList={userList}
          project={project}
          name="owner"
        />
      )}
      <h4>Redaktion</h4>
      {project && userList && (
        <UserSelect
          handleSelectChange={handleSelectChange}
          userList={userList}
          project={project}
          name="writer"
        />
      )}
      <h4>Motion Design</h4>
      {userList && (
        <UserSelect
          handleSelectChange={handleSelectChange}
          userList={userList}
          project={project}
          name="motionDesign"
        />
      )}

      <Button type="button" onClick={onClickHandler}>
        Abbrechen
      </Button>

      {formData.title.trim() && formData.customer.trim() ? (
        <Button>Speichern</Button>
      ) : (
        <Button disabled>Speichern</Button>
      )}
    </ProjectDetails>
  )
}

export default ProjectDetailsEdit

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
