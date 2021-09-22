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

const NewUserPage: FC = () => {
  const { token } = useContext(AuthContext)
  const history = useHistory()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<RestExceptionDto>()
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
        .then(() => {
          setLoading(false)
          history.push('/users')
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
  return (
    <PageLayout>
      <Header />
      <main>
        {loading && <Loader />}
        {!loading && (
          <NewUserForm onSubmit={handleFormOnSubmit}>
            <input
              name="loginName"
              type="text"
              placeholder="Name"
              value={formData.loginName}
              onChange={handleInputOnChange}
            />
            <select
              name="role"
              value={formData.role}
              onChange={handleOnSelectChange}
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
            <Button disabled={!formData.loginName.trim()}>Speichern</Button>
          </NewUserForm>
        )}
        {error && (
          <ErrorPopup
            message={error.message}
            resetErrorState={resetErrorState}
          />
        )}
      </main>
    </PageLayout>
  )
}
export default NewUserPage

const NewUserForm = styled.form`
  display: grid;
  grid-gap: var(--size-l);
  justify-items: center;
`
