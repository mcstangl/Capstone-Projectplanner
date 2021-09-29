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
import {
  findAllUser,
  moveToArchive,
  updateProject,
} from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import { ProjectDto } from '../dtos/ProjectDto'
import AuthContext from '../auth/AuthContext'
import { UserDto } from '../dtos/UserDto'
import { UpdateProjectDto } from '../dtos/UpdateProjectDto'
import Loader from './Loader'
import { useHistory } from 'react-router-dom'
import { EditInputField } from './EditInputField'

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
  dateOfReceipt: string
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
  const history = useHistory()
  const [userList, setUserList] = useState<UserDto[]>()
  const [loading, setLoading] = useState(true)

  const [formData, setFormData] = useState<UpdateProjektFormData>({
    customer: '',
    title: '',
    dateOfReceipt: '',
    writer: [],
    motionDesign: [],
  })

  useEffect(() => {
    if (token) {
      findAllUser(token)
        .then(setUserList)
        .catch(error => updateErrorState(error.response.data))
        .finally(() => setLoading(false))
    }
  }, [token, updateErrorState])

  useEffect(() => {
    if (project) {
      setFormData({
        customer: project.customer,
        title: project.title,
        dateOfReceipt: project.dateOfReceipt,
        motionDesign: project.motionDesign,
        writer: project.writer,
        owner: project.owner,
      })
    }
  }, [project])

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    setLoading(true)
    updateErrorState(undefined)
    if (
      project &&
      token &&
      formData.title.trim() &&
      formData.customer.trim() &&
      formData.owner
    ) {
      const updateProjectDto: UpdateProjectDto = {
        title: project.title,
        newTitle: formData.title.trim(),
        customer: formData.customer.trim(),
        status: project.status,
        dateOfReceipt: formData.dateOfReceipt,
        owner: formData.owner,
        writer: formData.writer,
        motionDesign: formData.motionDesign,
      }
      updateProject(updateProjectDto, token)
        .then(projectDto => {
          setLoading(false)
          updateProjectState(projectDto)
          switchEditMode()
        })
        .catch(error => {
          setLoading(false)
          updateErrorState(error.response.data)
        })
    }
  }

  const onClickHandler = () => {
    switchEditMode()
  }

  const handleMoveToArchiveOnClick = () => {
    if (token && project) {
      moveToArchive(token, project.title)
        .then(() => history.push('/projects'))
        .catch(error => updateErrorState(error.response.data))
    }
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
    <section>
      {loading && <Loader />}

      {!loading && project && userList && (
        <ProjectDetails onSubmit={submitHandler}>
          <h4>Eingangsdatum</h4>

          <EditInputField
            type="date"
            name="dateOfReceipt"
            value={formData.dateOfReceipt}
            placeholder={formData.dateOfReceipt}
            onChange={onChangeHandler}
          />

          <h4>Kunde</h4>

          <EditInputField
            name="customer"
            type="text"
            value={formData.customer}
            placeholder={formData.customer}
            onChange={onChangeHandler}
          />
          <h4>Title</h4>

          <EditInputField
            name="title"
            type="text"
            value={formData.title}
            placeholder={formData.title}
            onChange={onChangeHandler}
          />
          <h4>Projektleitung</h4>

          <UserSelect
            handleSelectChange={handleSelectChange}
            userList={userList}
            project={project}
            name="owner"
          />

          <h4>Redaktion</h4>

          <UserSelect
            handleSelectChange={handleSelectChange}
            userList={userList}
            project={project}
            name="writer"
          />

          <h4>Motion Design</h4>

          <UserSelect
            handleSelectChange={handleSelectChange}
            userList={userList}
            project={project}
            name="motionDesign"
          />

          <Button type="button" onClick={onClickHandler}>
            Abbrechen
          </Button>

          <Button
            disabled={!(formData.title.trim() && formData.customer.trim())}
          >
            Speichern
          </Button>
        </ProjectDetails>
      )}
      {!loading && (
        <MoveToArchiveButton
          type="button"
          theme="secondary"
          onClick={handleMoveToArchiveOnClick}
        >
          ins Archiv verschieben
        </MoveToArchiveButton>
      )}
    </section>
  )
}

export default ProjectDetailsEdit

const ProjectDetails = styled.form`
  display: grid;
  grid-template-columns: max-content max-content;
  grid-gap: var(--size-s);
  padding-bottom: 2rem;
  justify-content: center;

  h4 {
    justify-self: right;
    margin: 0;
    padding: 0.5rem 1rem 0.5rem 0;
  }
`
const MoveToArchiveButton = styled(Button)`
  margin: var(--size-l);
`
