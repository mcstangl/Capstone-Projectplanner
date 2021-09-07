import { FC, useContext } from 'react'
import AuthContext from '../auth/AuthContext'
import styled from 'styled-components/macro'

const Header: FC = () => {
  const { authUser, logout } = useContext(AuthContext)
  return (
    <header>
      <h3>Project Planner</h3>
      {authUser && logout && (
        <section>
          {'Sie sind eingeloggt als, '}
          <span>{authUser.loginName}</span>
          <Logout onClick={logout}> ausloggen</Logout>
        </section>
      )}
    </header>
  )
}
export default Header

const Logout = styled.p`
  color: cornflowerblue;

  &:hover {
    color: lightblue;
    cursor: pointer;
  }
`
