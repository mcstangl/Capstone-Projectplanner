import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Button } from '../components/Button'
import { useParams } from 'react-router-dom'
import styled from 'styled-components/macro'
import { UserDto } from '../dtos/UserDto'
import AuthContext from '../auth/AuthContext'
import { findUserByLoginName } from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import ErrorPopup from '../components/ErrorPopup'
import Loader from '../components/Loader'
import UserDetailsEdit from '../components/UserDetailsEdit'

interface RouteParams {
  loginName: string
}

const UserDetailPage: FC = () => {
  const { loginName } = useParams<RouteParams>()
  const { token } = useContext(AuthContext)

  const [editMode, setEditMode] = useState(false)
  const [error, setError] = useState<RestExceptionDto>()
  const [loading, setLoading] = useState(true)
  const [user, setUser] = useState<UserDto>()

  useEffect(() => {
    if (token && loginName) {
      findUserByLoginName(token, loginName)
        .then(setUser)
        .catch(error => setError(error.response.data))
        .finally(() => setLoading(false))
    }
  }, [token, loginName])

  const resetEditMode = () => {
    if (editMode) {
      setEditMode(false)
    } else setEditMode(true)
  }
  const resetErrorState = () => {
    setError(undefined)
  }

  return (
    <PageLayout>
      <Header />
      <main>
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
          <UserDetailsEdit user={user} resetEditMode={resetEditMode} />
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
export default UserDetailPage

const UserDetailsStyle = styled.section`
  display: grid;
  grid-template-columns: max-content 1fr;
  grid-gap: var(--size-s);
`
