import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import AuthContext from '../auth/AuthContext'
import { findUserByLoginName } from '../service/api-service'
import { RestExceptionDto } from '../dtos/RestExceptionDto'
import MyAccountDetails from '../components/MyAccountDetails'
import Loader from '../components/Loader'

const MyAccountPage: FC = () => {
  const { authUser, token } = useContext(AuthContext)
  const [user, setUser] = useState()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<RestExceptionDto>()

  useEffect(() => {
    if (token && authUser && typeof authUser.loginName === 'string') {
      findUserByLoginName(token, authUser.loginName)
        .then(user => {
          setUser(user)
        })
        .catch(error => {
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
        .finally(() => setLoading(false))
    }
  }, [token, authUser])

  const resetErrorState = () => {
    setError(undefined)
  }
  return (
    <PageLayout>
      <Header />
      <main>
        {loading && <Loader />}
        {!loading && user && (
          <MyAccountDetails
            user={user}
            error={error}
            resetErrorState={resetErrorState}
            loading={loading}
          />
        )}
      </main>
    </PageLayout>
  )
}
export default MyAccountPage
