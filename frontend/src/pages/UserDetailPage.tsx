import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { useParams } from 'react-router-dom'
import { UserDto } from '../dtos/UserDto'
import AuthContext from '../auth/AuthContext'
import { findUserByLoginName } from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import UserDetail from '../components/UserDetail'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import { LinkStyle } from '../components/LinkStyle'
import MainStyle from '../components/MainStyle'

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

  const fetchUser = () => {
    if (token && loginName) {
      return findUserByLoginName(token, loginName).then(setUser)
    }
  }

  return (
    <PageLayout>
      <Header />
      <MainStyle>
        <ButtonGroupFlexbox>
          <LinkStyle to="/users">Zurück zur Liste</LinkStyle>
        </ButtonGroupFlexbox>
        {user && (
          <UserDetail
            fetchUser={fetchUser}
            loading={loading}
            user={user}
            resetErrorState={resetErrorState}
            error={error}
          />
        )}
      </MainStyle>
    </PageLayout>
  )
}
export default UserDetailPage
