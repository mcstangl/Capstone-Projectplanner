import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { Button } from './Button'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import ErrorPopup from './ErrorPopup'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import Loader from './Loader'
import AuthContext from '../auth/AuthContext'
import { updateUser } from '../service/api-service'
import { useHistory } from 'react-router-dom'

interface UserDetailEditProps {
  user: UserDto
  resetEditMode: () => void
}

const UserDetailsEdit: FC<UserDetailEditProps> = ({ user, resetEditMode }) => {
  const { token } = useContext(AuthContext)
  const history = useHistory()
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState<UserDto>({
    loginName: user.loginName,
    role: user.role,
  })

  const handleInputOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, loginName: event.target.value })
  }

  const handleSelectOnChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setFormData({ ...formData, role: event.target.value })
  }

  const handleFromOnSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (token && formData.loginName.trim()) {
      setLoading(true)
      const userDto: UserDto = {
        loginName: formData.loginName.trim(),
        role: formData.role,
      }
      updateUser(token, user.loginName, userDto)
        .then((updatedUser: UserDto) => {
          setLoading(false)
          resetEditMode()
          history.push(`/users/${updatedUser.loginName}`)
        })
        .catch(error => {
          setLoading(false)
          if (error.response.data) {
            setError(error.response.data)
          } else
            setError({
              message: error.response.status + ': ' + error.response.statusText,
            })
        })
    }
  }

  const resetErrorSate = () => setError(undefined)

  return (
    <section>
      {loading && <Loader />}
      {!loading && (
        <UserEditStyle onSubmit={handleFromOnSubmit}>
          <span>Login Name</span>
          <input
            type="text"
            placeholder={user.loginName}
            value={formData.loginName}
            onChange={handleInputOnChange}
          />
          <span>User Rolle</span>
          <select defaultValue={formData.role} onChange={handleSelectOnChange}>
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
          </select>

          <Button disabled={!formData.loginName.trim()}>Speichern</Button>
          <Button>Passwort zurücksetzen</Button>
          <Button onClick={resetEditMode}>Abbrechen</Button>
        </UserEditStyle>
      )}
      {error && (
        <ErrorPopup message={error.message} resetErrorState={resetErrorSate} />
      )}
    </section>
  )
}
export default UserDetailsEdit

const UserEditStyle = styled.form`
  display: grid;
  grid-template-columns: max-content 1fr;
  grid-gap: var(--size-s);
`
