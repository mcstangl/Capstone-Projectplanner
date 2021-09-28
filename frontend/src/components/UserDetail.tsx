import { FC, useState } from 'react'
import Loader from './Loader'
import { Button } from './Button'
import UserDetailsEdit from './UserDetailsEdit'
import ErrorPopup from './ErrorPopup'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import { RestExceptionDto } from '../dtos/RestExceptionDto'

interface UserDetailProps {
  user: UserDto
  error?: RestExceptionDto
  resetErrorState: () => void
  fetchUser: () => Promise<void> | undefined
  loading?: boolean
}

const UserDetail: FC<UserDetailProps> = ({
  user,
  loading,
  error,
  resetErrorState,
  fetchUser,
}) => {
  const [editMode, setEditMode] = useState(false)

  const resetEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }

  return (
    <section>
      {loading && <Loader />}
      {!loading && !editMode && user && (
        <UserDetailsStyle>
          <span>Login Name</span>
          <span>{user.loginName}</span>
          <span>User Rolle</span>
          <span>{user.role}</span>
          <Button onClick={resetEditMode}>Edit</Button>
        </UserDetailsStyle>
      )}

      {editMode && user && (
        <UserDetailsEdit
          fetchUser={fetchUser}
          user={user}
          resetEditMode={resetEditMode}
        />
      )}
      {error && (
        <ErrorPopup message={error.message} resetErrorState={resetErrorState} />
      )}
    </section>
  )
}
export default UserDetail

const UserDetailsStyle = styled.section`
  display: grid;
  grid-template-columns: max-content 1fr;
  grid-gap: var(--size-s);

  span {
    justify-self: right;
    margin: 0;
    padding: 0.5rem 1rem 0.5rem 0;
  }
  span:nth-child(2n) {
    padding: 0.5rem;
    justify-self: left;
  }
`
