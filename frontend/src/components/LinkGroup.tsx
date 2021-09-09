import styled from 'styled-components/macro'

export const LinkGroup = styled.section`
  display: grid;
  justify-items: center;
  a {
    text-decoration: none;
    border: solid var(--maincolor) 1px;
    color: var(--maincolor);
    padding: 0.5rem 2rem;
    border-radius: 4px;
  }

  a:hover {
    background-color: var(--maincolor);
    color: white;
  }
`
