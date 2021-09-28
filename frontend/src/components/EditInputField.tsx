import styled from 'styled-components/macro'

export const EditInputField = styled.input`
  font-size: 100%;
  padding: 0.5rem;
  border: 0;
  border-bottom: 1px solid var(--secondarycolor);

  &:focus {
    border-bottom: 2px solid var(--accentcolor);
    outline: 0;
  }
`
