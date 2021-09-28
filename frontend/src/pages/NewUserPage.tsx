import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Button } from '../components/Button'
import styled from 'styled-components/macro'
import { NewUserDto } from '../dtos/NewUserDto'
import AuthContext from '../auth/AuthContext'
import { createNewUser } from '../service/api-service'
import { useHistory } from 'react-router-dom'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import Loader from '../components/Loader'
import ErrorPopup from '../components/ErrorPopup'
import { UserWithPasswordDto } from '../dtos/UserWithPasswordDto'
import { InputField } from '../components/Inputfield'
import { SelectStyle } from '../components/SelectStyle'
import NewPasswordPopup from '../components/NewPasswordPopup'

const NewUserPage: FC = () => {
  const { token } = useContext(AuthContext)
  const history = useHistory()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<RestExceptionDto>()
  const [newUser, setNewUser] = useState<UserWithPasswordDto>()
  const [formData, setFormData] = useState<NewUserDto>({
    loginName: '',
    role: 'USER',
  })

  const handleFormOnSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (token && formData.loginName.trim()) {
      const newUserDto: NewUserDto = {
        loginName: formData.loginName.trim(),
        role: formData.role,
      }
      setLoading(true)
      createNewUser(token, newUserDto)
        .then((newUser: UserWithPasswordDto) => {
          setLoading(false)
          setNewUser(newUser)
        })
        .catch(error => {
          setLoading(false)
          setError(error.response.data)
        })
    }
  }

  const resetErrorState = () => setError(undefined)

  const handleInputOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, loginName: event.target.value })
  }

  const handleOnSelectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setFormData({ ...formData, role: event.target.value })
  }

  const handleNewPasswordPopupOnClick = () => {
    setNewUser(undefined)
    history.push('/users')
  }

  return (
    <PageLayout>
      <Header />
      <main>
        {loading && <Loader />}
        {!loading && (
          <NewUserForm onSubmit={handleFormOnSubmit}>
            <section>
              <label htmlFor="loginName">Benutzername</label>
              <InputField
                id="loginName"
                name="loginName"
                type="text"
                placeholder="Name"
                value={formData.loginName}
                onChange={handleInputOnChange}
              />
            </section>

            <section>
              <label htmlFor="role">Benutzerrolle</label>
              <SelectStyle
                id="role"
                name="role"
                value={formData.role}
                onChange={handleOnSelectChange}
              >
                <option value="USER">User</option>
                <option value="ADMIN">Admin</option>
              </SelectStyle>
            </section>

            <Button disabled={!formData.loginName.trim()}>Speichern</Button>
          </NewUserForm>
        )}
        {error && (
          <ErrorPopup
            message={error.message}
            resetErrorState={resetErrorState}
          />
        )}
        {newUser && (
          <NewPasswordPopup
            newUser={newUser}
            handleNewPasswordPopupOnClick={handleNewPasswordPopupOnClick}
          />
        )}
      </main>
    </PageLayout>
  )
}
export default NewUserPage

const NewUserForm = styled.form`
  display: grid;
  grid-template-columns: max-content;
  grid-gap: var(--size-l);
  justify-content: center;

  section {
    width: 100%;
    display: grid;
    justify-content: center;
  }
`
