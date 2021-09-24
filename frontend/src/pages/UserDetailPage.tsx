import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { useParams } from 'react-router-dom'
import { UserDto } from '../dtos/UserDto'
import AuthContext from '../auth/AuthContext'
import { findUserByLoginName } from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import UserDetail from '../components/UserDetail'

interface RouteParams {
  loginName: string
}

const UserDetailPage: FC = () => {
  const { loginName } = useParams<RouteParams>()
  const { token } = useContext(AuthContext)

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

  const resetErrorState = () => {
    setError(undefined)
  }

  return (
    <PageLayout>
      <Header />
      <main>
        {user && (
          <UserDetail
            loading={loading}
            user={user}
            resetErrorState={resetErrorState}
            error={error}
          />
        )}
      </main>
    </PageLayout>
  )
}
export default UserDetailPage
