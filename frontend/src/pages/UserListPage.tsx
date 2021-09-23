import { FC, useContext, useEffect, useState } from 'react'
import { PageLayout } from '../components/PageLayout'
import Header from '../components/Header'
import { ButtonGroupFlexbox } from '../components/ButtonGroupFlexbox'
import styled from 'styled-components/macro'
import { Link, useHistory } from 'react-router-dom'
import { UserDto } from '../dtos/UserDto'
import AuthContext from '../auth/AuthContext'
import { findAllUser } from '../service/api-service'
import Loader from '../components/Loader'

const UserListPage: FC = () => {
  const { token, authUser } = useContext(AuthContext)
  const [user, setUser] = useState<UserDto[]>()
  const history = useHistory()
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (token && authUser) {
      findAllUser(token)
        .then((userList: UserDto[]) => {
          const filteredUserList = userList.filter(
            user => user.loginName !== authUser?.loginName
          )
          setUser(filteredUserList)
        })
        .catch(console.error)
        .finally(() => setLoading(false))
    }
  }, [token, authUser])

  return (
    <PageLayout>
      <Header />
      <main>
        <ButtonGroupFlexbox>
          {!loading && (
            <LinkStyle to="/new-user">Neuen Benutzer anlegen</LinkStyle>
          )}
        </ButtonGroupFlexbox>
        {loading && <Loader />}
        {!loading && user && (
          <UserList>
            <UserListHeader>
              <span>Name</span>
              <span>Rolle</span>
            </UserListHeader>

            {user?.map(user => (
              <UserListItem
                key={user.loginName}
                onClick={() => history.push('/users/' + user.loginName)}
              >
                <span>{user.loginName}</span>
                <span>{user.role}</span>
              </UserListItem>
            ))}
          </UserList>
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
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-gap: 0 var(--size-s);
`

const UserListHeader = styled.section`
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  border-bottom: solid 1px var(--secondarycolor);
  margin-bottom: var(--size-s);
`

const UserListItem = styled.section`
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-column-gap: var(--size-s);
  padding: 0.5rem;
  text-decoration: none;
  color: black;

  &:hover {
    background: var(--gradient4);
  }
`
