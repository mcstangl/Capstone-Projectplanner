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
import { useHistory } from 'react-router-dom'
import { Button } from '../components/Button'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import { UserDto } from '../dtos/UserDto'
import Loader from '../components/Loader'
import MainStyle from '../components/MainStyle'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import { InputField } from '../components/Inputfield'
import { LinkStyle } from '../components/LinkStyle'
import { SelectStyle } from '../components/SelectStyle'

interface NewProjectFormData {
  customer: string
  title: string
  owner?: UserDto
  dateOfReceipt?: string
}

const NewProjectPage: FC = () => {
  const { token } = useContext(AuthContext)
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(true)
  const [userList, setUserList] = useState<UserDto[]>()
  const [formData, setFormData] = useState<NewProjectFormData>({
    customer: '',
    title: '',
    dateOfReceipt: '',
  })

  const history = useHistory()

  useEffect(() => {
    if (token) {
      findAllUser(token)
        .then(setUserList)
        .catch(setError)
        .finally(() => setLoading(false))
    }
  }, [token])

  const submitHandler = (event: FormEvent) => {
    event.preventDefault()
    setLoading(true)
    setError(undefined)
    if (
      token &&
      formData.owner &&
      formData.customer.trim() &&
      formData.title.trim() &&
      formData.dateOfReceipt
    ) {
      const newProjectDto: NewProjectDto = {
        owner: formData.owner,
        customer: formData.customer.trim(),
        title: formData.title.trim(),
        dateOfReceipt: formData.dateOfReceipt,
      }
      createNewProject(newProjectDto, token)
        .then(() => {
          setLoading(false)
          history.push('/projects')
        })
        .catch(error => {
          setLoading(false)
          setError(error.response.data)
        })
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

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [event.target.name]: event.target.value })
  }

  const handleDateChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, dateOfReceipt: event.target.value })
  }

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        <ButtonGroupFlexbox>
          <LinkStyle to="/projects">Zurück zur Liste</LinkStyle>
        </ButtonGroupFlexbox>
        {loading && <Loader />}
        {!loading && (
          <ProjectForm onSubmit={submitHandler}>
            <section>
              <label htmlFor="customer">Kundenname</label>
              <InputField
                id="customer"
                name="customer"
                type="text"
                value={formData.customer}
                onChange={handleInputChange}
              />
            </section>

            <section>
              <label htmlFor="title">Titel</label>
              <InputField
                id="title"
                name="title"
                type="text"
                value={formData.title}
                onChange={handleInputChange}
              />
            </section>

            <section>
              <label htmlFor="owner">Projektleitung</label>
              <SelectStyle
                id="owner"
                onChange={handleSelectChange}
                defaultValue={'DEFAULT'}
              >
                <option value="DEFAULT" disabled>
                  ...bitte auswählen
                </option>
                {userList?.map(user => (
                  <option key={user.loginName} value={user.loginName}>
                    {user.loginName}
                  </option>
                ))}
              </SelectStyle>
            </section>

            <section>
              <label htmlFor="dateOfReceipt">Eingangsdatum</label>
              <InputField
                id="dateOfReceipt"
                type="date"
                value={formData.dateOfReceipt}
                onChange={handleDateChange}
              />
            </section>

            {formData.customer.trim() &&
            formData.title.trim() &&
            formData.owner &&
            formData.dateOfReceipt ? (
              <Button>Speichern</Button>
            ) : (
              <Button disabled>Speichern</Button>
            )}
            {error && <p>{error.message}</p>}
          </ProjectForm>
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default NewProjectPage

const ProjectForm = styled.form`
  display: grid;
  grid-template-columns: max-content;
  grid-gap: var(--size-l);
  justify-content: center;

  section {
    width: 100%;
    display: grid;
    justify-items: center;
  }
`
