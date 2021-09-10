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
import styled from 'styled-components/macro'
import { NewProjectDto } from '../dtos/NewProjectDto'
import { createNewProject, findAllUser } from '../service/api-service'
import AuthContext from '../auth/AuthContext'
import { Link, useHistory } from 'react-router-dom'
import { Button } from '../components/Button'
import { LinkGroup } from '../components/LinkGroup'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import { UserDto } from '../dtos/UserDto'

const NewProjectPage: FC = () => {
  const { token } = useContext(AuthContext)
  const [error, setError] = useState<RestExceptionDto>()
  const [userList, setUserList] = useState<UserDto[]>()
  const [formData, setFormData] = useState<NewProjectDto>({
    customer: '',
    title: '',
    ownerName: '',
  })

  const history = useHistory()

  useEffect(() => {
    if (token) {
      findAllUser(token).then(setUserList).catch(setError)
    }
  }, [token])

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    setError(undefined)
    if (token && formData.customer.trim() && formData.title.trim()) {
      const newProjectDto: NewProjectDto = {
        ownerName: formData.ownerName,
        customer: formData.customer.trim(),
        title: formData.customer.trim(),
      }
      createNewProject(newProjectDto, token)
        .then(() => history.push('/projects'))
        .catch(error => setError(error.response.data))
    }
  }
  const handleSelectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setFormData({ ...formData, ownerName: event.target.value })
  }

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  return (
    <PageLayout>
      <Header />
      <main>
        <LinkGroup>
          <Link to="/projects">Zurück zur Liste</Link>
        </LinkGroup>

        <ProjectForm onSubmit={submitHandler}>
          <input
            name="customer"
            type="text"
            placeholder="Kundenname"
            value={formData.customer}
            onChange={handleInputChange}
          />
          <input
            name="title"
            type="text"
            placeholder="Projekt Titel"
            value={formData.title}
            onChange={handleInputChange}
          />
          <select onChange={handleSelectChange}>
            <option selected disabled>
              ...bitte auswählen
            </option>
            {userList?.map(user => (
              <option key={user.loginName} value={user.loginName}>
                {user.loginName}
              </option>
            ))}
          </select>
          {formData.customer.trim() && formData.title.trim() ? (
            <Button>Speichern</Button>
          ) : (
            <Button disabled>Speichern</Button>
          )}
          {error && <p>{error.message}</p>}
        </ProjectForm>
      </main>
    </PageLayout>
  )
}
export default NewProjectPage

const ProjectForm = styled.form`
  display: grid;
  grid-gap: var(--size-l);
  justify-items: center;
`
