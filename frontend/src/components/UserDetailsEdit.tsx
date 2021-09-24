import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { Button } from './Button'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import ErrorPopup from './ErrorPopup'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import Loader from './Loader'
import AuthContext from '../auth/AuthContext'
import {
  deleteUser,
  resetUserPassword,
  updateUser,
} from '../service/api-service'
import { useHistory } from 'react-router-dom'
import { UserWithPasswordDto } from '../dtos/UserWithPasswordDto'

interface UserDetailEditProps {
  user: UserDto
  resetEditMode: () => void
  fetchUser: () => Promise<void> | undefined
}

const UserDetailsEdit: FC<UserDetailEditProps> = ({
  user,
  resetEditMode,
  fetchUser,
}) => {
  const { token } = useContext(AuthContext)
  const history = useHistory()
  const [error, setError] = useState<RestExceptionDto>()
  const [deleteMode, setDeleteMode] = useState(false)
  const [loading, setLoading] = useState(false)
  const [newPassword, setNewPassword] = useState<string>()
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
        .then(() => {
          setLoading(false)
          resetEditMode()
        })
        .then(() => fetchUser())
        .then(() => history.push(`/users/${userDto.loginName}`))
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

  const handleResetPasswordOnClick = () => {
    if (token) {
      setLoading(true)
      resetUserPassword(token, user.loginName)
        .then((user: UserWithPasswordDto) => {
          setLoading(false)
          setNewPassword(user.password)
        })
        .catch(error => {
          setLoading(false)
          if (error.response.data.message) {
            setError(error.response.data)
          } else if (error.response.data.error) {
            setError({
              message:
                error.response.data.status + ': ' + error.response.data.error,
            })
          } else
            setError({
              message: error.response.status + ': ' + error.response.statusText,
            })
        })
    }
  }

  const handleNewPasswordPopupOnClick = () => {
    setNewPassword(undefined)
    resetEditMode()
  }
  const handleDeletePopupOnClick = () => {
    if (token) {
      setLoading(true)
      deleteUser(token, user.loginName)
        .then(() => {
          setLoading(false)
          history.push('/users')
        })
        .catch(error => {
          setLoading(false)
          setDeleteMode(false)
          if (error.response.data.message) {
            setError(error.response.data)
          } else if (error.response.data.error) {
            setError({
              message:
                error.response.data.status + ': ' + error.response.data.error,
            })
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
          <Button type="button" onClick={handleResetPasswordOnClick}>
            Passwort zurücksetzen
          </Button>
          <Button
            type="button"
            theme="secondary"
            onClick={() => setDeleteMode(true)}
          >
            Löschen
          </Button>
          <Button type="button" onClick={resetEditMode}>
            Abbrechen
          </Button>
        </UserEditStyle>
      )}
      {error && (
        <ErrorPopup message={error.message} resetErrorState={resetErrorSate} />
      )}
      {newPassword && (
        <PopupStyle>
          <p>Temporäres Passwort für Benutzer {user.loginName}</p>
          <p>{newPassword}</p>
          <Button theme="secondary" onClick={handleNewPasswordPopupOnClick}>
            OK
          </Button>
        </PopupStyle>
      )}
      {deleteMode && (
        <PopupStyle>
          <h3>Benutzer</h3>
          <p>{user.loginName}</p>
          <Button theme="secondary" onClick={handleDeletePopupOnClick}>
            Löschen
          </Button>
          <Button theme="secondary" onClick={() => setDeleteMode(false)}>
            Abbrechen
          </Button>
        </PopupStyle>
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
const PopupStyle = styled.section`
  position: absolute;
  background-color: white;
  right: 0;
  left: 0;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
  width: 250px;
  display: grid;
  grid-template-columns: 100%;
  justify-items: center;
  grid-gap: var(--size-l);
  border: 1px solid var(--secondarycolor);
  box-shadow: 3px 8px 12px grey;
  padding: var(--size-l);

  button {
    width: 100%;
  }
`
