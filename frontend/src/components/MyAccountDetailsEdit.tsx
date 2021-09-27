import { ChangeEvent, FC, FormEvent, useContext, useState } from 'react'
import { Button } from './Button'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import ErrorPopup from './ErrorPopup'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import Loader from './Loader'
import AuthContext from '../auth/AuthContext'
import { updatePassword, updateUser } from '../service/api-service'
import { useHistory } from 'react-router-dom'
import { UserWithPasswordDto } from '../dtos/UserWithPasswordDto'

interface MyAccountDetailsProps {
  user: UserDto
  resetEditMode: () => void
}

const MyAccountDetailsEdit: FC<MyAccountDetailsProps> = ({
  user,
  resetEditMode,
}) => {
  const { token, logout } = useContext(AuthContext)
  const history = useHistory()
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(false)
  const [updatePasswordMode, setUpdatePasswordMode] = useState(false)
  const [newPasswordFormData, setNewPasswordFormData] = useState({
    password: '',
    passwordRepeated: '',
  })
  const [formData, setFormData] = useState<UserDto>({
    loginName: user.loginName,
    role: user.role,
  })

  const handleInputOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, loginName: event.target.value })
  }

  const handleNewPasswordOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNewPasswordFormData({
      ...newPasswordFormData,
      [event.target.name]: event.target.value,
    })
  }

  const handleFromOnSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (token && logout && formData.loginName.trim()) {
      setLoading(true)
      const userDto: UserDto = {
        loginName: formData.loginName.trim(),
        role: formData.role,
      }
      updateUser(token, user.loginName, userDto)
        .then((updatedUser: UserDto) => {
          setLoading(false)
          resetEditMode()
          if (updatedUser.loginName !== user.loginName) {
            logout()
          } else history.push(`/my-account`)
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

  const handleNewPasswordPopupOnClick = () => {
    if (updatePasswordMode) {
      setUpdatePasswordMode(false)
    } else setUpdatePasswordMode(true)
  }

  const handleNewPasswordFromSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (token && logout) {
      setLoading(true)
      setUpdatePasswordMode(false)
      const userWithPasswordDto: UserWithPasswordDto = {
        loginName: user.loginName,
        password: newPasswordFormData.password.trim(),
        role: user.role,
      }
      updatePassword(token, userWithPasswordDto)
        .then(() => logout())
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

          <Button disabled={!formData.loginName.trim()}>Speichern</Button>

          <Button type="button" onClick={handleNewPasswordPopupOnClick}>
            Passwort ändern
          </Button>

          <Button type="button" onClick={resetEditMode}>
            Abbrechen
          </Button>
        </UserEditStyle>
      )}
      {error && (
        <ErrorPopup message={error.message} resetErrorState={resetErrorSate} />
      )}
      {updatePasswordMode && (
        <NewPasswordPopupStyle onSubmit={handleNewPasswordFromSubmit}>
          <span>Bitte geben sie ein neues Passwort ein</span>
          <input
            name="password"
            type="password"
            value={newPasswordFormData.password}
            onChange={handleNewPasswordOnChange}
          />
          <span>Passwort wiederholen</span>
          <input
            name="passwordRepeated"
            type="password"
            value={newPasswordFormData.passwordRepeated}
            onChange={handleNewPasswordOnChange}
          />
          <Button
            theme="secondary"
            disabled={
              !(
                newPasswordFormData.password ===
                  newPasswordFormData.passwordRepeated &&
                newPasswordFormData.password.trim()
              )
            }
          >
            OK
          </Button>
          <Button
            type="button"
            theme="secondary"
            onClick={handleNewPasswordPopupOnClick}
          >
            Abbrechen
          </Button>
        </NewPasswordPopupStyle>
      )}
    </section>
  )
}
export default MyAccountDetailsEdit

const UserEditStyle = styled.form`
  display: grid;
  grid-template-columns: max-content;
  grid-gap: var(--size-s);
`

const NewPasswordPopupStyle = styled.form`
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
