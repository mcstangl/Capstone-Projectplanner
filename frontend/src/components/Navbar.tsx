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
          <NavLink to="/">Homepage</NavLink>
          <NavLink to="/projects">Projekte</NavLink>
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

  a:hover {
    color: var(--secondarycolor);
    transition-duration: 300ms;
  }
`
