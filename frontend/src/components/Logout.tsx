import { FC, useContext } from 'react'
import AuthContext from '../auth/AuthContext'
import styled from 'styled-components/macro'

const Logout: FC = () => {
  const { authUser, logout } = useContext(AuthContext)

  return (
    <Wrapper>
      {authUser && logout && (
        <>
          <span>{'Angemeldet als ' + authUser.loginName + ', '}</span>
          <LogoutLink onClick={logout}> ausloggen</LogoutLink>
        </>
      )}
    </Wrapper>
  )
}
export default Logout

const Wrapper = styled.section`
  display: flex;
  justify-content: flex-end;
  padding: 0.5rem 0;
`

const LogoutLink = styled.span`
  color: var(--maincolor);
  transition-duration: 300ms;
  margin-left: var(--size-xs);

  &:hover {
    color: var(--gradient4);
    cursor: pointer;
    transition-duration: 300ms;
  }
`
