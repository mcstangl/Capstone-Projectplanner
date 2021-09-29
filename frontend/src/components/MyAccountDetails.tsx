import { FC, useState } from 'react'
import { Button } from './Button'
import ErrorPopup from './ErrorPopup'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import MyAccountDetailsEdit from './MyAccountDetailsEdit'

interface MyAccountDetailsProps {
  user: UserDto
  error?: RestExceptionDto
  resetErrorState: () => void
  loading?: boolean
  adminMode?: boolean
}

const MyAccountDetails: FC<MyAccountDetailsProps> = ({
  user,
  error,
  resetErrorState,
  adminMode,
}) => {
  const [editMode, setEditMode] = useState(false)

  const resetEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <section>
      {!editMode && user && (
        <UserDetailsStyle>
          <span>Benutzername</span>
          <span>{user.loginName}</span>
          {adminMode && <span>User Rolle</span>}
          {adminMode && <span>{user.role}</span>}
          <Button onClick={resetEditMode}>Edit</Button>
        </UserDetailsStyle>
      )}

      {editMode && user && (
        <MyAccountDetailsEdit user={user} resetEditMode={resetEditMode} />
      )}
      {error && (
        <ErrorPopup message={error.message} resetErrorState={resetErrorState} />
      )}
    </section>
  )
}
export default MyAccountDetails

const UserDetailsStyle = styled.section`
  display: grid;
  grid-template-columns: 250px;
  grid-gap: var(--size-l);
  justify-content: center;

  span {
    padding: 0.5rem;
  }
`
