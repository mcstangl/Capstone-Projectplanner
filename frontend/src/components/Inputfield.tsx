import styled from 'styled-components/macro'

export const InputField = styled.input`
  width: 100%;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid var(--darkgrey);
  &:focus {
    border: 2px solid var(--accentcolor);
    outline: 0;
  }
`
