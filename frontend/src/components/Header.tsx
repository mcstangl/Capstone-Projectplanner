import { FC } from 'react'
import styled from 'styled-components/macro'
import Logout from './Logout'
import Navbar from './Navbar'
import Logo from './Logo'

const Header: FC = () => {
  return (
    <HeaderStyle>
      <Logo />
      <Logout />
      <h3>Projekt Planung</h3>
      <Navbar />
    </HeaderStyle>
  )
}
export default Header

const HeaderStyle = styled.header`
  box-shadow: 1px 0 4px grey;
  margin-bottom: var(--size-xxl);
  display: grid;
  grid-template-columns: 150px 1fr;
  grid-template-rows: 80px 1fr;
  padding-bottom: var(--size-m);
  padding-left: var(--size-m);
  padding-right: var(--size-m);

  h3 {
    color: var(--maincolor);
    margin: 0;
  }
`
