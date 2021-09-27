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
  const [newPassword, setNewPassword] = useState<string>()
  const [formData, setFormData] = useState<UserDto>({
    loginName: user.loginName,
    role: user.role,
  })

  const handleInputOnChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, loginName: event.target.value })
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
    setNewPassword(undefined)
    resetEditMode()
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

          <Button>Passwort ändern</Button>

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
    </section>
  )
}
export default MyAccountDetailsEdit

const UserEditStyle = styled.form`
  display: grid;
  grid-template-columns: max-content;
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
