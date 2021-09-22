import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { Button } from '../components/Button'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import styled from 'styled-components/macro'
import { Link } from 'react-router-dom'
import { UserDto } from '../dtos/UserDto'
import AuthContext from '../auth/AuthContext'
import { findAllUser } from '../service/api-service'
import Loader from '../components/Loader'

const UserListPage: FC = () => {
  const { token } = useContext(AuthContext)
  const [user, setUser] = useState<UserDto[]>()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (token) {
      findAllUser(token)
        .then(setUser)
        .catch(console.error)
        .finally(() => setLoading(false))
    }
  }, [token])

  return (
    <PageLayout>
      <Header />
      <main>
        <ButtonGroupFlexbox>
          {!loading && <LinkStyle to="/">Neuen Benutzer anlegen</LinkStyle>}
        </ButtonGroupFlexbox>
        {loading && <Loader />}
        {!loading && user && (
          <section>
            <UserList>
              <UserListHeader>
                <span>Name</span>
                <span>Rolle</span>
              </UserListHeader>

              {user?.map(user => (
                <UserListItem key={user.loginName}>
                  <span>{user.loginName}</span>
                  <span>{user.role}</span>
                </UserListItem>
              ))}
            </UserList>
            <section>
              <Button>Edit</Button>
              <Button>Passwort zurücksetzen</Button>
              <Button>Abbrechen</Button>
            </section>
          </section>
        )}
      </main>
    </PageLayout>
  )
}
export default UserListPage

const LinkStyle = styled(Link)`
  text-decoration: none;
  border: solid var(--maincolor) 1px;
  color: var(--maincolor);
  padding: 0.5rem 2rem;
  border-radius: 4px;
  transition-duration: 300ms;

  &:hover {
    background-color: var(--maincolor);
    color: white;
    transition-duration: 300ms;
  }
`

const UserList = styled.section`
  display: grid;
`

const UserListHeader = styled.section`
  display: grid;
  grid-template-columns: 1fr 1fr;
`

const UserListItem = styled.section`
  display: grid;
  grid-template-columns: 1fr 1fr;
`
