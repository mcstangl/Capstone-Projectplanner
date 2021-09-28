import styled from 'styled-components/macro'
import { Link } from 'react-router-dom'

export const LinkStyle = styled(Link)`
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
