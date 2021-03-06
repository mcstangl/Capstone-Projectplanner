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
import { EditInputField } from './EditInputField'
import { EditSelect } from './EditSelect'

interface UserDetailEditProps {
  user: UserDto
  resetEditMode: () => void
}

const UserDetailsEdit: FC<UserDetailEditProps> = ({ user, resetEditMode }) => {
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
          <EditInputField
            type="text"
            placeholder={user.loginName}
            value={formData.loginName}
            onChange={handleInputOnChange}
          />
          <span>User Rolle</span>

          <EditSelect>
            <select
              defaultValue={formData.role}
              onChange={handleSelectOnChange}
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </EditSelect>

          <Button type="button" onClick={handleResetPasswordOnClick}>
            Passwort zur??cksetzen
          </Button>

          <Button disabled={!formData.loginName.trim()}>Speichern</Button>

          <Button
            type="button"
            theme="secondary"
            onClick={() => setDeleteMode(true)}
          >
            L??schen
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
          <p>Tempor??res Passwort f??r Benutzer {user.loginName}</p>
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
            L??schen
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
  grid-template-columns: max-content max-content;
  grid-gap: var(--size-s);
  justify-content: center;

  span {
    justify-self: right;
    margin: 0;
    padding: 0.5rem 1rem 0.5rem 0;
  }
`
const PopupStyle = styled.section`
  position: fixed;
  background-color: white;
  top: 30%;
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
