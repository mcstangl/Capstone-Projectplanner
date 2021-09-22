import { FC, useContext } from 'react'
import { NavLink } from 'react-router-dom'
import styled from 'styled-components/macro'
import AuthContext from '../auth/AuthContext'

const Navbar: FC = () => {
  const { authUser } = useContext(AuthContext)

  return (
    <section>
      {authUser && (
        <Wrapper>
          <NavLink exact to="/">
            Homepage
          </NavLink>
          <NavLink to="/projects">Projekte</NavLink>
          {authUser.role === 'ADMIN' && <NavLink to="/users">Benutzer</NavLink>}
        </Wrapper>
      )}
    </section>
  )
}
export default Navbar

const Wrapper = styled.nav`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;

  a {
    text-decoration: none;
    color: var(--darkgrey);
    transition-duration: 300ms;
    padding: 0 2rem;
  }

  a.active {
    color: var(--maincolor);
  }

  a.active:hover {
    color: #54aba0;
  }

  a:hover {
    color: var(--secondarycolor);
    transition-duration: 300ms;
  }
`
